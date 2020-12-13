package com.dhbw.btreebackend.btreeimplementation;

import com.dhbw.btreebackend.BTreeBackendApplication;

import java.util.ArrayList;

/**
 * A class representing a BTree.
 * Contains the order of the BTree.
 * Contains a reference to the BTree's root node.
 * Contains elementsMax indicating the maximum number of elements a node can contain.
 * Contains elementsMin indicating the minimum number of elements a node has to contain.
 *
 * Offers public access methods to insert, delete or search for element keys, to access or set the BTree's order and
 *      root, to clear the BTree, and to get all element keys ordered ascending.
 * Contains private methods to balance the tree after insertion or deletion of elements.
 *
 * @author Julian Stein
 * @version 0.9
 */
public class BTree {
    private int order;
    private Node root;
    private int elementsMax;
    private int elementMin;

    public BTree(int order) {
        setOrder(order);
    }

    /**
     * Search for the location of given key.
     * @param elementKey the key to search for.
     * @return BTreeSearchResult object containing information on whether the element was found,
     *         where it was found, the element itself and how many nodes had to be accessed.
     *         If this.root == null, return a BTreeSearchResult object with a false result and null values for
     *         the references.
     */
    public BTreeSearchResult searchElement(int elementKey) {
        if(this.root == null) {
            return new BTreeSearchResult();
        }
        Node inspectedNode = this.root;
        int costs = 1;
        while(true) {
            Element elementWithKeyInInspectedNode = inspectedNode.getElementWithKey(elementKey);
            if(elementWithKeyInInspectedNode != null) {     // inspected node contains key
                return new BTreeSearchResult(inspectedNode, elementWithKeyInInspectedNode, costs);
            } else if(inspectedNode.isLeaf()) {             // inspected node does not contain key and is leaf
                return new BTreeSearchResult(inspectedNode, null, costs);
            } else {                                        // keep traversing tree
                inspectedNode = inspectedNode.getRootOfSubtreeForElementKey(elementKey);
            }
            ++costs;
        }
    }

    /**
     * Insert a new element with the given key into the BTree.
     * If the tree is empty create a new root node and add te new element to it.
     * Otherwise search for insert position and insert new element the normal way.
     * @param elementKey the key to insert.
     */
    public boolean insertElement(int elementKey) {
        if(root == null) {
            this.root = new Node(null);
            this.root.addElement(new Element(elementKey));
            return true;
        } else {
            BTreeSearchResult insertPosition = searchElement(elementKey);
            if(!insertPosition.isFound()) {
                insertPosition.getLocation().addElement(new Element(elementKey));
                checkOverflow(insertPosition.getLocation());
                return true;
            }
            return false;
        }
    }

    /**
     * Check whether an overflow occured in the given node.
     * If so, split the node and recursively call checkOverflow with parent node until a node without an overflow is
     * reached or the root was processed.
     * @param inspectedNode the node to check.
     */
    private void checkOverflow(Node inspectedNode) {
        if(inspectedNode != null && inspectedNode.getNumberOfElements() > this.elementsMax) {
            Node parentNode = splitNode(inspectedNode);
            checkOverflow(parentNode);
        }
    }

    /**
     * Split the given node into two new nodes. Create a new root if necessary.
     * @param toSplit the node to split.
     * @return the parent node of the split node. Might be a newly created root.
     */
    private Node splitNode(Node toSplit) {
        int splitIndex = (toSplit.getNumberOfElements() / 2);
        Element splitElement = toSplit.getElements().get(splitIndex);
        Node parentNode;
        if(toSplit == this.root) {
            parentNode = new Node(null);
            this.root = parentNode;
        } else {
            parentNode = toSplit.getParentNode();
        }
        Node rightNode = new Node(parentNode, toSplit.getGreaterSplitSublistOfElements(splitIndex));
        rightNode.setChildrenParent();
        splitElement.setRightNode(rightNode);

        toSplit.setParentNode(parentNode);
        toSplit.setElements(toSplit.getSmallerSplitSublistOfElements(splitIndex));
        splitElement.setLeftNode(toSplit);

        parentNode.addElement(splitElement);
        return parentNode;
    }

    /**
     * Check whether the BTree contains the given element. If so, delete it and initiate underflow-check on leaf element
     * where delete calls ended.
     * @param elementKey the elementKey to delete.
     * @return true, if the elementKey was found and deleted, false if the BTree does not contain the elementKey.
     */
    public boolean deleteElement(int elementKey) {
        BTreeSearchResult bTreeSearchResult = searchElement(elementKey);
        if(bTreeSearchResult.isFound()) {
            Node balancingStart = bTreeSearchResult.getLocation().deleteElement(bTreeSearchResult.getElement());
            checkUnderflow(balancingStart);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check whether an underflow occured in the given node.
     * If so and the given node is not the BTree's root,
     *  delegate processing based on the given node's neighbours, which are determined getting the separator elements
     *  in the parent node, and their elements:
     *  If one of the neighbours has more than the minimum number of elements, perform a rotation.
     *      If both neighbours qualify for a rotation, perform a rightwards rotation using the left neighbour.
     *  If none of the neighbours has more than the minimum number of elements, merge the node into one of his neighbours.
     *      If the node has two neighbours, merge the node into his left neighbour.
     * If the node given node the root and has no elements left, set the BTree's root to null. The BTree is now empty.
     * @param inspectedNode the node to check.
     */
    private void checkUnderflow(Node inspectedNode) {
        if(inspectedNode != this.root && inspectedNode.getNumberOfElements() < this.elementMin) {
            Element[] neighbourSeparators = inspectedNode.getNeighbourSeparators();
            Node leftNeighbour = neighbourSeparators[0] != null ? neighbourSeparators[0].getLeftNode() : null;
            Node rightNeighbour = neighbourSeparators[1] != null ? neighbourSeparators[1].getRightNode() : null;
            if(leftNeighbour != null && leftNeighbour.getNumberOfElements() > elementMin) {
                // has left neighbour and left neighbour has more than minimum number of elements --> rotate right
                rotateRight(leftNeighbour, inspectedNode);
            } else if(rightNeighbour != null && rightNeighbour.getNumberOfElements() > elementMin) {
                // has right neighbour and right neighbour has more than minimum number of elements --> rotate left
                rotateLeft(inspectedNode, rightNeighbour);
            } else if(leftNeighbour != null) {
                // has left neighbour --> merge into left neighbour
                mergeRightIntoLeftNode(leftNeighbour, inspectedNode, neighbourSeparators[0], neighbourSeparators[1]);
            } else if(rightNeighbour != null) {
                // has right neighbour --> merge into right neighbour
                mergeLeftIntoRightNode(inspectedNode, rightNeighbour, neighbourSeparators[1], neighbourSeparators[0]);
            }
        } else if(inspectedNode == this.root && inspectedNode.getNumberOfElements() < 1) {
            // no elements left in root at this point --> last element was deleted --> BTree is empty
            this.root = null;
        }
    }

    /**
     * Perform a rightwards rotation using the given nodes.
     * Move the element separating the two nodes in the parent node to the left edge of the right node.
     * Move the greatest element of the left node to the position of the former separator.
     * Adjust references accordingly. Doing so, check the right node for a phantomRef if it contains no elements and use
     *      it as the former separator's new right child node. Set the right node's phantomRef to null afterwards.
     * Adjust the right node's (old and) new children's parentNode references to reference the right node.
     *
     * After finishing the rotation the BTree is balanced.
     * @param left the left node to move away elements from.
     * @param right the right node to move elements to (node with underflow).
     */
    private void rotateRight(Node left, Node right) {
        Node parentNode = left.getParentNode();
        Element separator = parentNode.getSeparatorElementForChildNodes(left, right);
        if(separator != null) {     // always true because of the way parameters have been determined
            Element greatestOfLeft = left.getGreatestElement();
            Element smallestOfRight = right.getSmallestElement();
            Node rightChildOfGreatestOfLeft = greatestOfLeft.getRightNode();
            greatestOfLeft.setLeftNode(separator.getLeftNode());
            greatestOfLeft.setRightNode(separator.getRightNode());
            separator.setLeftNode(rightChildOfGreatestOfLeft);
            separator.setRightNode((smallestOfRight != null) ? smallestOfRight.getLeftNode() : right.getPhantomRef());
            right.setPhantomRef(null);
            right.addElement(separator);
            right.setChildrenParent();
            parentNode.replaceElement(separator, greatestOfLeft);
            left.dropElement(greatestOfLeft);
        }
    }

    /**
     * Perform a leftwards rotation using the given nodes.
     * Move the element separating the two nodes in the parent node to the right edge of the left node.
     * Move the smallest element of the right node to the position of the former separator.
     * Adjust references accordingly. Doing so, check the left node for a phantomRef if it contains no elements and use
     *      it as the former separator's new left child node. Set the left node's phantomRef to null afterwards.
     * Adjust the left node's (old and) new children's parentNode references to reference the left node.
     *
     * After finishing the rotation the BTree is balanced.
     * @param left the left node to move elements to (node with underflow).
     * @param right the right node to move elements away from.
     */
    private void rotateLeft(Node left, Node right) {
        Node parentNode = left.getParentNode();
        Element separator = parentNode.getSeparatorElementForChildNodes(left, right);
        if(separator != null) {     // always true because of the way parameters have been determined
            Element greatestOfLeft = left.getGreatestElement();
            Element smallestOfRight = right.getSmallestElement();
            Node leftChildOfSmallestElementOfRight = smallestOfRight.getLeftNode();
            smallestOfRight.setLeftNode(separator.getLeftNode());
            smallestOfRight.setRightNode(separator.getRightNode());
            separator.setLeftNode((greatestOfLeft != null) ? greatestOfLeft.getRightNode() : left.getPhantomRef());
            separator.setRightNode(leftChildOfSmallestElementOfRight);
            left.setPhantomRef(null);
            left.addElement(separator);
            left.setChildrenParent();
            parentNode.replaceElement(separator, smallestOfRight);
            right.dropElement(smallestOfRight);
        }
    }

    /**
     * Merge the given right node into the given left node moving down and sandwiching the element
     *      separating the two nodes in the parent node.
     * Move the former separator to the right edge of the left node.
     * Append the elements of the right node to the right edge of the left node.
     * Adjust references accordingly.
     *      Doing so, check if the separator has an element to his right. If so, set separatorsRightNeighbourElement's
     *          left child reference to the merge result as the previous left child no longer exists after the merge.
     *      Doing so, check the right node for a phantomRef if it contains no elements and use
     *          it the as the former separators new right child node. Set the right node's phantomRef to null afterwards.
     * Adjust the left node's (old and) new children's parentNode references to reference the left node.
     *
     * If the parentNode of the two nodes is the root and is left with zero elements after the merge, the merge result
     *      becomes the root of the BTree.
     * If the parentNode of the two nodes is not the root:
     *      If the parent node is left with zero elements, set its phantomRef to the merge result.
     *      Call checkUnderflow with the parentNode to rebalance the BTree from there if necessary.
     * @param left the node to merge the right node into.
     * @param right the node to merge into the left node; (node with underflow).
     * @param separator the element separating the given left and right node in the parent node.
     * @param separatorsRightNeighbourElement the right neighbour element of the separator in the parent node. Can be
     *          null if the right node has no right neighbour.
     */
    private void mergeRightIntoLeftNode(Node left, Node right,
                                        Element separator, Element separatorsRightNeighbourElement) {
        Node parentNode = left.getParentNode();
        if(separator != null) {     // always true because of the way parameters have been determined, however check included
            Element greatestOfLeft = left.getGreatestElement();
            Element smallestOfRight = right.getSmallestElement();
            if(separatorsRightNeighbourElement != null) {
                separatorsRightNeighbourElement.setLeftNode(left);
            }
            separator.setLeftNode(greatestOfLeft.getRightNode());
            separator.setRightNode((smallestOfRight != null) ? smallestOfRight.getLeftNode() : right.getPhantomRef());
            right.setPhantomRef(null);
            left.appendElement(separator);
            left.appendElements(right.getElements());
            left.setChildrenParent();
            parentNode.dropElement(separator);

            if(parentNode == this.root && parentNode.getNumberOfElements() == 0) {
                this.root = left;
            } else if(parentNode != this.root){
                if(parentNode.getNumberOfElements() == 0) {
                    parentNode.setPhantomRef(left);
                }
                checkUnderflow(parentNode);
            }
        }
    }

    /**
     * Merge the given left node into the given right node moving down and sandwiching the element
     *      separating the two nodes in the parent node.
     * Move the former separator to the left edge of the right node.
     * Prepend the elements of the left node to the left edge of the right node.
     * Adjust references accordingly.
     *      Doing so, check if the separator has an element to his left. If so, set separatorsLeftNeighbourElement's
     *          right child reference to the merge result as the previous right child no longer exists after the merge.
     *      Doing so, check the left node for a phantomRef if it contains no elements and use
     *          it the as the former separators new left child node. Set the left node's phantomRef to null afterwards.
     * Adjust the right node's (old and) new children's parentNode references to reference the right node.
     *
     * If the parentNode of the two nodes is the root and is left with zero elements after the merge, the merge result
     *      becomes the root of the BTree.
     * If the parentNode of the two nodes is not the root:
     *      If the parent node is left with zero elements, set its phantomRef to the merge result.
     *      Call checkUnderflow with the parentNode to rebalance the BTree from there if necessary.
     * @param left the node to merge into the right node; (node with underflow).
     * @param right the node to merge the left node into.
     * @param separator the element separating the given left and right node in the parent node.
     * @param separatorsLeftNeighbourElement the left neighbour element of the separator in the parent node. Can be
     *          null if the left node has no left neighbour.
     *
     * In the current balancing-implementation @param separatorsLeftNeighbourElement will always be null as this merge-
     * method will only get called if the left node has no left neighbour. However, for completeness and to keep the
     * possibility to switch things around the function has been implemented completely.
     */
    private void mergeLeftIntoRightNode(Node left, Node right,
                                        Element separator, Element separatorsLeftNeighbourElement) {
        Node parentNode = left.getParentNode();
        if(separator != null) {     // always true because of the way parameters have been determined, however check included
            Element greatestOfLeft = left.getGreatestElement();
            Element smallestOfRight = right.getSmallestElement();
            if(separatorsLeftNeighbourElement != null) {
                separatorsLeftNeighbourElement.setRightNode(right);
            }
            separator.setLeftNode((greatestOfLeft != null) ? greatestOfLeft.getRightNode() : left.getPhantomRef());
            separator.setRightNode(smallestOfRight.getLeftNode());
            left.setPhantomRef(null);
            right.prependElement(separator);
            right.prependElements(left.getElements());
            right.setChildrenParent();
            parentNode.dropElement(separator);

            if(parentNode == this.root && parentNode.getNumberOfElements() == 0) {
                this.root = right;
            } else if(parentNode != this.root){
                if(parentNode.getNumberOfElements() == 0) {
                    parentNode.setPhantomRef(right);
                }
                checkUnderflow(parentNode);
            }
        }
    }

    /**
     * Clear the BTree by setting its root to null and reset order to default 5.
     */
    public void clear() {
        this.root = null;
        this.setOrder(BTreeBackendApplication.DEFAULT_ORDER);
    }

    /**
     * Get all keys contained in the BTree ordered ascending.
     * @return all contained keys ordered ascending.
     */
    public ArrayList<Integer> getAllElementKeysOrderedAscending() {
        return this.root.getAllElementKeysOfSubtreeOrderedAscending();
    }

    /**
     * Set this.order and calculate and set this.elementsMax and this.elementsMin based on order.
     * Clear the BTree's root and insert all previously contained element keys in a loop.
     * @param order new order to set.
     */
    public void setOrder(int order) {
        ArrayList<Integer> keys = new ArrayList<>();
        if(this.root != null) {
            keys = getAllElementKeysOrderedAscending();
            this.root = null;
        }
        this.order = order;
        this.elementsMax = order - 1;
        this.elementMin = (order / 2 + ((order % 2 == 0) ? 0 : 1)) - 1;
        for(int key : keys) {
            this.insertElement(key);
        }
    }

    /* Standard getters and setters */

    public int getOrder() {
        return this.order;
    }

    public Node getRoot() {
        return this.root;
    }
}

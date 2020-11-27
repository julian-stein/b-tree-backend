package com.dhbw.btreebackend.btreeimplementation;

public class BTree {
    private int order;
    private Node root;
    private int elementsMax;
    private int elementMin;

    public BTree(int order) {
        setOrder(order);
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
     * Check whether an overflow occured in the given node.
     * If so, split the node and recursivly call checkOverflow with parent node until a node without an overflow is reached
     * the root was processed.
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

    private void checkUnderflow(Node inspectedNode) {
        if(inspectedNode != this.root && inspectedNode.getNumberOfElements() < this.elementMin) {
            Node[] neighbours = inspectedNode.getNeighbours();
            if(neighbours[0] != null && neighbours[0].getNumberOfElements() > elementMin) {
                // has left neighbour and left neighbour has more than minimum number of elements --> rotate right
                rotateRight(neighbours[0], inspectedNode);
            } else if(neighbours[1] != null && neighbours[1].getNumberOfElements() > elementMin) {
                // has right neighbour and right neighbour has more than minimum number of elements --> rotate left
                rotateLeft(inspectedNode, neighbours[1]);
            } else if(neighbours[0] != null) {
                mergeRightInLeftNode(neighbours[0], inspectedNode);
            } else if(neighbours[1] != null) {
                mergeLeftInRightNode(inspectedNode, neighbours[1]);
            }
        } else if(inspectedNode == this.root && inspectedNode.getNumberOfElements() < 1) {
            this.root = null;
        }
    }

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

            // checkUnderflow(parentNode);
        }
    }

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

            // checkUnderflow(parentNode);
        }
    }

    /**
     * right is underflow
     * @param left
     * @param right
     */
    private void mergeRightInLeftNode(Node left, Node right) {
        Node parentNode = left.getParentNode();
        Element separator = parentNode.getSeparatorElementForChildNodes(left, right);
        if(separator != null) {     // always true because of the way parameters have been determined, however check included
            Element greatestOfLeft = left.getGreatestElement();
            Element smallestOfRight = right.getSmallestElement();
            separator.setLeftNode(greatestOfLeft.getRightNode());
            separator.setRightNode((smallestOfRight != null) ? smallestOfRight.getLeftNode() : right.getPhantomRef());
            right.setPhantomRef(null);
            left.appendElement(separator);
            left.appendElements(right.getElements());
            left.setChildrenParent();
            parentNode.dropElement(separator);

            if(parentNode == this.root && parentNode.getNumberOfElements() == 0) {
                this.root = left;
            } else {
                if(parentNode.getNumberOfElements() == 0) {
                    parentNode.setPhantomRef(left);
                }
                checkUnderflow(parentNode);
            }
        }
    }

    /**
     * left ist underflow
     * @param left
     * @param right
     */
    private void mergeLeftInRightNode(Node left, Node right) {
        Node parentNode = left.getParentNode();
        Element separator = parentNode.getSeparatorElementForChildNodes(left, right);
        if(separator != null) {     // always true because of the way parameters have been determined, however check included
            Element greatestOfLeft = left.getGreatestElement();
            Element smallestOfRight = right.getSmallestElement();
            separator.setLeftNode((greatestOfLeft != null) ? greatestOfLeft.getRightNode() : left.getPhantomRef());
            separator.setRightNode(smallestOfRight.getLeftNode());
            left.setPhantomRef(null);
            right.prependElement(separator);
            right.prependElements(left.getElements());
            right.setChildrenParent();
            parentNode.dropElement(separator);

            if(parentNode == this.root && parentNode.getNumberOfElements() == 0) {
                this.root = right;
            } else {
                if(parentNode.getNumberOfElements() == 0) {
                    parentNode.setPhantomRef(right);
                }
                checkUnderflow(parentNode);
            }
        }
    }

    /**
     * Set this.order and calculate and set this.elementsMax and this.elementsMin based on order
     * @param order new order to set.
     */
    public void setOrder(int order) {
        //TODO: rebuild tree
        this.order = order;
        this.elementsMax = order - 1;
        this.elementMin = (order / 2 + ((order % 2 == 0) ? 0 : 1)) - 1;
    }

    public int getOrder() {
        return this.order;
    }

    public Node getRoot() {
        return this.root;
    }

    public static void main(String[] args) {
        BTree myTree = new BTree(3);
        for(int i = 1; i < 18; ++i) {
            myTree.insertElement(i);
        }
        myTree.deleteElement(10);
        myTree.insertElement(10);
        myTree.deleteElement(10);
    }
}

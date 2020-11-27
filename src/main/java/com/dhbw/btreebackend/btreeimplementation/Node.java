package com.dhbw.btreebackend.btreeimplementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Node {
    private List<Element> elements;
    private Node parentNode;
    private Node phantomRef;
    private UUID uuid;

    public Node(Node parentNode) {
        this.parentNode = parentNode;
        this.elements = new ArrayList<>();
        this.uuid = UUID.randomUUID();
    }
    public Node(Node parentNode, List<Element> elements) {
        this.parentNode = parentNode;
        this.elements = new ArrayList<>();
        this.elements.addAll(elements);
        this.uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get a sublist of this node's elements from index 0 (inclusive) up to index splitIndex(exclusive).
     * @param splitIndex integer indicating the upper bound (exclusive).
     * @return list of elements.
     */
    public List<Element> getSmallerSplitSublistOfElements(int splitIndex) {
        // TODO: exceptionhandling/check index bounds
        return this.elements.subList(0, splitIndex);
    }

    /**
     * Get a sublist of this node's elements from index splitIndex (exclusive) up to .
     * @param splitIndex integer indicating the upper bound (exclusive).
     * @return list of elements.
     */
    public List<Element> getGreaterSplitSublistOfElements(int splitIndex) {
        // TODO: exceptionhandling//check index bounds
        return this.elements.subList((splitIndex+1), getNumberOfElements());
    }

    /**
     * Set this as parent of all children. Necessary after some elements have been moved to this node after split.
     * If this is a leaf node, do nothing as this has no children.
     */
    public void setChildrenParent() {
        if(!isLeaf()) {
            for (Element element : this.elements) {
                element.getLeftNode().setParentNode(this);
            }
            this.elements.get(getNumberOfElements() - 1).getRightNode().setParentNode(this);
        }
    }

    /**
     * Public access method to add a new element to the node. Delegates depending on whether this node is a leaf or not.
     * @param newElement the element to add.
     */
    public void addElement(Element newElement) {
        if(this.isLeaf()) {
            addLeafElement(newElement);
        } else {
            addNonLeafElement(newElement);
        }

    }

    /**
     * Append a new element to this node's elements and sort the elements afterwards. As leaf-elements contain no
     * references, no references to child nodes have to be adjusted.
     * @param newElement the element to add.
     */
    private void addLeafElement(Element newElement) {
        // TODO: reconsider efficiency, maybe use addNonLeafElement instead
        this.elements.add(newElement);
        Collections.sort(this.elements);
    }

    /**
     * Add a new element to this node's element at the correct position. As non-leaf elements contain references,
     * references have to be adjusted.
     * @param newElement the element to add.
     */
    private void addNonLeafElement(Element newElement) {
        boolean inserted = false;
        for(int i = 0; i < this.elements.size(); ++i) {
            if (newElement.getKey() < this.elements.get(i).getKey()) {
                elements.add(i, newElement);
                this.elements.get(i+1).setLeftNode(newElement.getRightNode());
                if(i != 0) {    // not inserted at start
                    this.elements.get(i-1).setRightNode(newElement.getLeftNode());
                }
                inserted = true;
                break;
            }
        }
        if(!inserted) {         // insert at end
            this.elements.add(newElement);
            this.elements.get(getNumberOfElements()-2).setRightNode(newElement.getLeftNode());
        }
    }

    /**
     * Append a list of elements at the end of this.elements.
     * @param newElements list of elements to append.
     */
    public void appendElements(List<Element> newElements) {
        this.elements.addAll(newElements);
    }

    /**
     * Prepend a list of elements at the beginning of this.elements.
     * @param newElements list of elements to prepend.
     */
    public void prependElements(List<Element> newElements) {
        newElements.addAll(this.elements);
        this.elements = newElements;
    }

    /**
     * Append a single element at the end of this.elements.
     * @param toAppend element to append.
     */
    public void appendElement(Element toAppend) {
        this.elements.add(toAppend);
    }

    /**
     * Prepend a single element at the beginning of this.elements.
     * @param toPrepend element to prepend.
     */
    public void prependElement(Element toPrepend) {

        this.elements.add(0, toPrepend);
    }

    /**
     * Delete the given element from this node.
     * If this is a leaf node, just remove the element.
     * If this is an inner node:
     *      - Find the node containing the greatest element of elementToDelete's left subtree
     *      - Replace elementToDelete with with the greatest element of the left subtree
     *      - Drop the replacement element from its node and return the node for the BTree to start balancing on it.
     * @param elementToDelete the element to delete.
     * @return the leaf node from which the replacement element was removed on which the BTree has to start re-balancing.
     */
    public Node deleteElement(Element elementToDelete) {
        if(isLeaf()) {
            this.elements.remove(elementToDelete);
            return this;
        } else {
            Node nodeWithGreatestElementInLeftSubtree = elementToDelete.getLeftNode().
                    getNodeContainingLargestElementInSubtree();
            Element replacement = nodeWithGreatestElementInLeftSubtree.getGreatestElement();
            replacement.setLeftNode(elementToDelete.getLeftNode());
            replacement.setRightNode(elementToDelete.getRightNode());
            nodeWithGreatestElementInLeftSubtree.dropElement(replacement);
            replaceElement(elementToDelete, replacement);
            return nodeWithGreatestElementInLeftSubtree;
        }
    }

    /**
     * Get the node containing the greatest element in the subtree starting with this node as root.
     * If this is a leaf: return this.
     * Else: pseudo-recursively call getNodeContainingLargestElementInSubtree() on the the most right child of this node.
     * @return the leaf node containing the largest element in this node's subtree.
     */
    public Node getNodeContainingLargestElementInSubtree() {
        if(isLeaf()) {
            return this;
        } else {
            return getGreatestElement().getRightNode().getNodeContainingLargestElementInSubtree();
        }
    }

    /**
     * Drop an element without any further processing.
     * @param elementToDrop the element to drop.
     */
    public void dropElement(Element elementToDrop) {
        this.elements.remove(elementToDrop);
    }

    public void replaceElement(Element oldElement, Element newElement) {
        this.elements.set(this.elements.indexOf(oldElement), newElement);
    }

    /**
     * If this is no root node (i.e. parentNode != null):
     *      Get the left and the right neighbour of this node by iterating over this.parentNode's elements until element
     *      referencing this node is found. Set neighbours based on whether this is left child of most left element in parent
     *      or right child of most right element in parent or in between.
     * @return two-element array of Nodes where Node at index 0 is the left neighbour and Node at index 1 is the right
     *         neighbour. Array elements can be null.
     */
    public Node[] getNeighbours() {
        Node[] neighbours = new Node[2];
        if(this.parentNode != null) {
            boolean found = false;
            // loop over parents elements
            for (int i = 0; !found && i < this.parentNode.getElements().size(); ++i) {
                Element currentElement = this.parentNode.getElements().get(i);
                if (currentElement.getLeftNode() == this) {
                    // if this is left child of most left element in parent --> no left neighbour
                    neighbours[0] = (i == 0) ? null : this.parentNode.getElements().get(i - 1).getLeftNode();
                    neighbours[1] = currentElement.getRightNode();
                    found = true;
                }
            }
            if (!found) {    // this is right child of most right element in parent --> no right neighbour
                neighbours[0] = this.parentNode.getElements().get(this.parentNode.getNumberOfElements() - 1).getLeftNode();
                neighbours[1] = null;
            }
        }
        return neighbours;
    }

    /**
     * Get the element of this node which separates the two given child nodes.
     * @param leftChild left child of the requested separator element.
     * @param rightChild right child of the requested separator element.
     * @return the separator element or null if no separator was found, which is excluded by caller.
     */
    public Element getSeparatorElementForChildNodes(Node leftChild, Node rightChild) {
        for(Element element : this.elements) {
            if(element.getLeftNode() == leftChild && element.getRightNode() == rightChild) {
                return element;
            }
        }
        return null;
    }

    /**
     * Checks whether this node is a leaf.
     * @return true if this node is a leaf, false otherwise.
     */
    public boolean isLeaf() {
        return elements.size() == 0 || elements.get(0).getLeftNode() == null;
    }

    public Element getElementWithKey(int elementKey) {
        for(Element element : this.elements) {
            if(element.getKey() == elementKey) {
                return element;
            }
        }
        return null;
    }

    /**
     * Checks whether this node contains the given elementKey.
     * @param elementKey: the elementKey to check for.
     * @return true if this node contains the given key, false otherwise.
     */
    public boolean containsKey(int elementKey) {
        return getElementWithKey(elementKey) != null;
    }

    /**
     * Get the root node of the subtree of this node in which the given elementKey is/would be located.
     * @param elementKey:
     * @return root node of the subtree, null if this node is a leaf.
     * @throws IndexOutOfBoundsException thrown if the node contains no elements (in which case the node should no
     *          longer exist).
     */
    public Node getRootOfSubtreeForElementKey(int elementKey) throws IndexOutOfBoundsException{
        for(Element element : this.elements) {
            if(elementKey < element.getKey()) {
                return element.getLeftNode();
            }
        }
        return elements.get(elements.size() - 1).getRightNode();
    }

    /**
     * Get the element of this node with the smallest key.
     * @return the element containing the smallest key, null if this node has no elements.
     */
    public Element getSmallestElement() {
        return (this.elements.size() != 0) ? this.elements.get(0) : null;
    }

    /**
     * Get the element of this node with the greatest key.
     * @return the element containing the greatest key, null if this node has no elements.
     */
    public Element getGreatestElement() {
        return (this.elements.size() != 0) ? this.elements.get(this.elements.size() - 1) : null;
    }

    public List<Element> getElements() {
        return this.elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public Node getParentNode() {
        return this.parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public Node getPhantomRef() {
        return phantomRef;
    }

    public void setPhantomRef(Node phantomRef) {
        this.phantomRef = phantomRef;
    }

    public int getNumberOfElements() {
        return this.elements.size();
    }
}

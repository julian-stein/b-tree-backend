package com.dhbw.btreebackend.btreeimplementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private List<Element> elements;
    private Node parentNode;

    public Node(Node parentNode) {
        this.parentNode = parentNode;
        this.elements = new ArrayList<>();
    }
    public Node(Node parentNode, List<Element> elements) {
        this.parentNode = parentNode;
        this.elements = new ArrayList<>();
        this.elements.addAll(elements);
    }



    public List<Element> getSmallerSplitSublistOfElements(int splitIndex) {
        // TODO: exceptionhandling/check index bounds
        return this.elements.subList(0, splitIndex);
    }

    public List<Element> getGreaterSplitSublistOfElements(int splitIndex) {
        // TODO: exceptionhandling//check index bounds
        return this.elements.subList((splitIndex+1), getNumberOfElements());
    }

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
     * Checks whether this node is a leaf.
     * @return true if this node is a leaf, false otherwise.
     */
    public boolean isLeaf() {
        return elements.size() == 0 || elements.get(0).getLeftNode() == null;
    }

    /**
     * Checks whether this node contains the given elementKey.
     * @param elementKey: the elementKey to check for.
     * @return true if this node contains the given key, false otherwise.
     */
    public boolean containsKey(int elementKey) {
        for(Element element : this.elements) {
            if(element.getKey() == elementKey) {
                return true;
            }
        }
        return false;
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

    public int getNumberOfElements() {
        return this.elements.size();
    }
}

package com.dhbw.btreebackend.btreeimplementation;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private List<Element> elements;
    private Node parentNode;

    public Node(Node parentNode) {
        this.parentNode = parentNode;
        elements = new ArrayList<>();
    }

    public void addElement(Element element) {
        for(int i = 0; i < this.elements.size(); ++i) {
            if(element.getKey() < elements.get(i).getKey()) {
                elements.add(i, element);
            }
        }
    }

    /**
     * Checks whether this node is a leaf.
     * @return true if this node is a leaf, false otherwise.
     */
    public boolean isLeaf() {
        return elements.get(1).getLeftNode() == null;
    }

    /**
     * Check whether this node contains the given elementKey.
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
}

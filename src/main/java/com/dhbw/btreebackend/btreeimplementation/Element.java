package com.dhbw.btreebackend.btreeimplementation;

/**
 * This class represents a single element in a BTree's node.
 * Contains the key used to insert and order an element in a BTree or Node.
 * Contains references to a left and right child node attached to the element.
 *
 * Offers constructors and standard getters and setters.
 *
 * Implements the Comparable-Interface so Element objects can be compared based on their key attribute value.
 *
 * @author Julian Stein
 * @version 1.0
 */
public class Element implements Comparable<Element>{
    private int key;
    private Node leftNode;
    private Node rightNode;

    /**
     * Standard constructor providing all attributes as initial values.
     * @param key key value of the newly created Element.
     * @param leftNode reference to the left child of the newly created Element.
     * @param rightNode referene to the right child of the newly created Element.
     */
    public Element(int key, Node leftNode, Node rightNode) {
        this.key = key;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    /**
     * Standard constructor providing only the key value for the newly created Element.
     * @param key the key value for the newly created Element.
     */
    public Element(int key) {
        this.key = key;
    }

    /**
     * Override the Comparable-Interface's compareTo function in a way that comparing Element objects results in
     *      comparing their key attribute values.
     * @param o the element to compare this element to.
     * @return a negative integer, zero, or a positive integer as this element
     *         is less than, equal to, or greater than the specified element.
     */
    @Override
    public int compareTo(Element o) {
        return Integer.compare(this.key, o.getKey());
    }

    /* Standard getters and setters */

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }
}

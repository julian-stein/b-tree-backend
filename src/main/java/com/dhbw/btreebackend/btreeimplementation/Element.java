package com.dhbw.btreebackend.btreeimplementation;

public class Element implements Comparable<Element>{
    private int key;
    private Node leftNode;
    private Node rightNode;

    public Element(int value, Node leftNode, Node rightNode) {
        this.key = value;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    public Element(int value) {
        this.key = value;
    }

    @Override
    public int compareTo(Element o) {
        return Integer.compare(this.key, o.getKey());
    }

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

package com.dhbw.btreebackend.btreeimplementation;

public class BTree {
    private int order;
    private Node root;

    public BTree(int order) {
        this.order = order;
    }

    /*public void insertElement(int elementKey) {
        if(root == null) {
            this.root = new Node();
            this.root.addElement(new Element(elementKey));
        } else {
            // find insert position
            // insert
            // check overflow, if yes --> split --> rekursiv auf Pfad bis Wurzel
        }
    }*/

    public BTreeSearchResult searchElement(int elementKey) {
        // < ... <=
        Node inspectedNode = this.root;
        while(true) {
            if(inspectedNode.containsKey(elementKey)) {
                return new BTreeSearchResult(true, inspectedNode);
            } else if(inspectedNode.isLeaf()) {
                return new BTreeSearchResult(false, inspectedNode);
            } else {
                inspectedNode = inspectedNode.getRootOfSubtreeForElementKey(elementKey);
            }
        }
    }
}

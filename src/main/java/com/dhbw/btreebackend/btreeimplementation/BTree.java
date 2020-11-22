package com.dhbw.btreebackend.btreeimplementation;

import java.util.Random;

public class BTree {
    private int order;
    private Node root;

    public BTree(int order) {
        this.order = order;
    }

    public void insertElement(int elementKey) {
        if(root == null) {
            this.root = new Node(null);
            this.root.addElement(new Element(elementKey));
        } else {
            BTreeSearchResult insertPosition = searchElement(elementKey);
            if(!insertPosition.isFound()) {
                insertPosition.getLocation().addElement(new Element(elementKey));
                checkAndProcessOverflow(insertPosition.getLocation());
            }
        }
    }

    public BTreeSearchResult searchElement(int elementKey) {
        Node inspectedNode = this.root;
        int costs = 1;
        while(true) {
            if(inspectedNode.containsKey(elementKey)) {
                return new BTreeSearchResult(true, inspectedNode, costs);
            } else if(inspectedNode.isLeaf()) {
                return new BTreeSearchResult(false, inspectedNode, costs);
            } else {
                inspectedNode = inspectedNode.getRootOfSubtreeForElementKey(elementKey);
            }
            ++costs;
        }
    }

    private void checkAndProcessOverflow(Node inspectedNode) {
        if(inspectedNode != null && inspectedNode.getNumberOfElements() > this.order) {
            int splitIndex = (inspectedNode.getNumberOfElements() / 2);
            Element splitElement = inspectedNode.getElements().get(splitIndex);
            Node parentNode;
            if(inspectedNode == this.root) {
                parentNode = new Node(null);
                this.root = parentNode;
            } else {
                parentNode = inspectedNode.getParentNode();
            }
            Node leftNode = new Node(parentNode, inspectedNode.getSmallerSplitSublistOfElements(splitIndex));
            leftNode.setChildrenParent();
            Node rightNode = new Node(parentNode, inspectedNode.getGreaterSplitSublistOfElements(splitIndex));
            rightNode.setChildrenParent();
            splitElement.setLeftNode(leftNode);
            splitElement.setRightNode(rightNode);
            parentNode.addElement(splitElement);
            checkAndProcessOverflow(parentNode);
        }
    }

    public static void main(String[] args) {
        BTree myTree = new BTree(4);
        for(int i = 1; i < 18; ++i) {
            myTree.insertElement(i);
        }
    }
}

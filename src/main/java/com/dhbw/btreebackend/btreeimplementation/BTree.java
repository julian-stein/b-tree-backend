package com.dhbw.btreebackend.btreeimplementation;

public class BTree {
    private int order;
    private Node root;

    public BTree(int order) {
        this.order = order;
    }

    /**
     * Insert a new element with the given key into the BTree.
     * If the tree is empty create a new root node and add te new element to it.
     * Otherwise search for insert position and insert new element the normal way.
     * @param elementKey the key to insert.
     */
    public void insertElement(int elementKey) {
        if(root == null) {
            this.root = new Node(null);
            this.root.addElement(new Element(elementKey));
        } else {
            BTreeSearchResult insertPosition = searchElement(elementKey);
            if(!insertPosition.isFound()) {
                insertPosition.getLocation().addElement(new Element(elementKey));
                checkOverflow(insertPosition.getLocation());
            }
        }
    }

    /**
     * Search for the location of given key.
     * @param elementKey the key to search for.
     * @return BTreeSearchResult object containing information on whether the element was found,
     *         where it was found and how many nodes had to be accessed.
     */
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

    /**
     * Check whether an overflow occured in the given node.
     * If so, split the node and recursivly call checkOverflow with parent node until a node without an overflow is reached
     * the root was processed.
     * @param inspectedNode the node to check.
     */
    private void checkOverflow(Node inspectedNode) {
        if(inspectedNode != null && inspectedNode.getNumberOfElements() > this.order) {
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
        Node leftNode = new Node(parentNode, toSplit.getSmallerSplitSublistOfElements(splitIndex));
        leftNode.setChildrenParent();
        Node rightNode = new Node(parentNode, toSplit.getGreaterSplitSublistOfElements(splitIndex));
        rightNode.setChildrenParent();
        splitElement.setLeftNode(leftNode);
        splitElement.setRightNode(rightNode);
        parentNode.addElement(splitElement);
        return parentNode;
    }

    public static void main(String[] args) {
        BTree myTree = new BTree(4);
        for(int i = 1; i < 18; ++i) {
            myTree.insertElement(i);
        }
    }
}

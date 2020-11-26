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
     *         where it was found, the element itself and how many nodes had to be accessed.
     */
    public BTreeSearchResult searchElement(int elementKey) {
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

    public boolean deleteElement(int elementKey) {
        BTreeSearchResult bTreeSearchResult = searchElement(elementKey);
        if(bTreeSearchResult.isFound()) {
            Node balancingStart = bTreeSearchResult.getLocation().deleteElement(bTreeSearchResult.getElement());
            // TODO: balance
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set this.order and calculate and set this.elementsMax and this.elementsMin based on order
     * @param order new order to set.
     */
    public void setOrder(int order) {
        this.order = order;
        this.elementsMax = order - 1;
        this.elementMin = order / 2 + ((order % 2 == 0) ? 0 : 1);
    }

    public int getOrder() {
        return this.order;
    }

    public Node getRoot() {
        return this.root;
    }
}

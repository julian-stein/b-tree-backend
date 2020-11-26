package com.dhbw.btreebackend.json;

import com.dhbw.btreebackend.btreeimplementation.*;

import javax.json.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BTreeToJson {

    /**
     * This methods calculates the height of the B-Tree. It goes from the root to the most left leaf
     * and counts the levels.
     *
     * @param bTree: The B-Tree to get the tree height from.
     * @return treeHeight: The height of the B-Tree.
     */
    private static int getTreeHeight(BTree bTree) {
        int treeHeight = 1;
        if (bTree.getRoot() != null) {
            Node currentLeft = bTree.getRoot();
            while (currentLeft.getElements().size() != 0 && currentLeft.getElements().get(0).getLeftNode() != null) {
                ++treeHeight;
                currentLeft = currentLeft.getElements().get(0).getLeftNode();
            }
        } else {
            return 0;
        }
        return treeHeight;
    }

    /**
     * This method first reorders the tree into a level-ordered tree. Therefore, all nodes will be added to a List,
     * starting from the left in every row. This array then will be custom transformed into a JSON-file containing
     * the level-ordered tree, that can be transferred to the frontend.
     *
     * @param bTree: The B-Tree to transform into a JSON-File.
     * @return The Json representation of the tree.
     */
    public static JsonObject createBTreeJson(BTree bTree) {
        List<Node> levelOrderTree = new ArrayList<Node>();
        int treeHeight = getTreeHeight(bTree);
        int numberLeaves = 0;

        Node currentNode;
        Node lastNode;

        if (bTree.getRoot() != null) {
            levelOrderTree.add(bTree.getRoot());
            numberLeaves = 1;

            if (bTree.getRoot().getElements().get(0).getLeftNode() != null) {
                lastNode = bTree.getRoot();
                while (lastNode.getElements().get(lastNode.getElements().size() - 1).getRightNode() != null) {
                    lastNode = lastNode.getElements().get(lastNode.getElements().size() - 1).getRightNode();
                }
                currentNode = bTree.getRoot().getElements().get(0).getLeftNode();
                List<Node> previousRow = new ArrayList<Node>();
                List<Node> currentRow = new ArrayList<Node>();
                previousRow.add(bTree.getRoot());

                while (currentNode != lastNode) {

                    for (Node nodeIterator : previousRow) {
                        for (Element elementIterator : nodeIterator.getElements()) {
                            currentRow.add(elementIterator.getLeftNode());
                            currentRow.add(elementIterator.getRightNode());
                            currentNode = elementIterator.getRightNode();
                        }
                    }
                    List<Node> currentRowWithoutDuplicates = currentRow.stream()
                            .distinct()
                            .collect(Collectors.toList());
                    levelOrderTree.addAll(currentRowWithoutDuplicates);
                    numberLeaves = currentRowWithoutDuplicates.size();
                    previousRow = currentRowWithoutDuplicates;
                    currentRow.clear();
                }
            }
        }

        JsonObjectBuilder jsonTree = Json.createObjectBuilder();
        JsonArrayBuilder jsonTreeNodes = Json.createArrayBuilder();

        jsonTree.add("Order", bTree.getOrder())
                .add("Height", treeHeight)
                .add("NumberLeaves", numberLeaves);

        for (Node n : levelOrderTree) {
            jsonTreeNodes.add(createNodeJson(n));
        }
        jsonTree.add("Nodes", jsonTreeNodes);

        return jsonTree.build();
    }

    /**
     * This method transforms an Element-Object to a suitable JSON-representation.
     *
     * @param element
     * @return the JSON-representation of the element.
     */
    private static JsonObject createElementJson(Element element) {
        JsonObjectBuilder jO = Json.createObjectBuilder();
        jO.add("Value", element.getKey());
        if (element.getLeftNode() != null) {

            jO.add("Left", element.getLeftNode().getUuid().toString())
                    .add("Right", element.getRightNode().getUuid().toString());
        }
        return jO.build();
    }

    /**
     * This method transforms a Node-Object to a suitable JSON-representation and calls the createElementJasonArray-
     * method for the element-list.
     *
     * @param node
     * @return the JSON-representation of the node.
     */
    private static JsonObject createNodeJson(Node node) {
        return Json.createObjectBuilder()
                .add("UUID", node.getUuid().toString())
                .add("Elements", createElementArrayJson(node.getElements()))
                .build();
    }

    /**
     * This method transforms the element-list into a suitable JSON-representation and calls the createElementJson-
     * method for every element.
     *
     * @param elements
     * @return the JSON-representation of the element-list.
     */
    private static JsonArray createElementArrayJson(List<Element> elements) {
        JsonArrayBuilder elementList = Json.createArrayBuilder();
        for (Element e : elements) {
            elementList.add(createElementJson(e));
        }
        return elementList.build();
    }
}

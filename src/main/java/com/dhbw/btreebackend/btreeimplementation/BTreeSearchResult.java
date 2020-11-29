package com.dhbw.btreebackend.btreeimplementation;

/**
 * Class used to transfer the result of searching for an element key in a B-Tree.
 * Contains a boolean 'found' whether the key was found.
 * Contains a reference to a Node 'location' where the key was found or where the search terminated.
 * Contains a reference to an Element 'element' that represents the key.
 * Contains an integer 'costs' indicating how many nodes had to be inspected to (not) find the element key.
 *
 * @author Julian Stein
 * @version 0.3
 */
public class BTreeSearchResult {
    private boolean found;
    private Node location;
    private Element element;
    private int costs;

    /**
     * Constructor for a new BTreeSearchResult.
     * Boolean attribute 'found' is set based on element (true if element !=null).
     * @param location reference to a Node where the key was found or where the search terminated
     * @param element reference to an Element that represents the key.
     * @param costs integer indicating how many nodes had to be inspected to (not) find the element key.
     */
    public BTreeSearchResult(Node location, Element element, int costs) {
        this.location = location;
        this.costs = costs;
        setElement(element);
    }

    public BTreeSearchResult() {

    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public Node getLocation() {
        return location;
    }

    public void setLocation(Node location) {
        this.location = location;
    }

    public int getCosts() {
        return costs;
    }

    public void setCosts(int costs) {
        this.costs = costs;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
        this.found = element != null;
    }
}

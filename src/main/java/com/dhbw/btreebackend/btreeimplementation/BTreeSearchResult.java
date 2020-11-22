package com.dhbw.btreebackend.btreeimplementation;

/**
 * Class used to transfer the result of searching for an element key in a B-Tree.
 * Contains a boolean 'found' whether the key was found.
 * Contains a reference to a Node 'location' where the key was found or where the search terminated.
 * Contains an integer 'costs' indicating how many nodes had to be inspected to (not) find the element key.
 *
 * @author Julian Stein
 * @version 0.2
 */
public class BTreeSearchResult {
    private boolean found;
    private Node location;
    private int costs;

    public BTreeSearchResult(boolean found, Node location, int costs) {
        this.found = found;
        this.location = location;
        this.costs = costs;
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
}

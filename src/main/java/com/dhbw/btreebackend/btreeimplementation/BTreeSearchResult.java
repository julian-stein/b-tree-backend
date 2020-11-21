package com.dhbw.btreebackend.btreeimplementation;

/**
 * Class used to transfer the result of searching for an element key in a B-Tree.
 * Contains a boolean 'found' whether the key was found.
 * Contains a reference to a Node 'location' where the key was found or where the search terminated.
 *
 * @author Julian Stein
 * @version 0.1
 */
public class BTreeSearchResult {
    private boolean found;
    private Node location;

    public BTreeSearchResult(boolean found, Node location) {
        this.found = found;
        this.location = location;
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
}

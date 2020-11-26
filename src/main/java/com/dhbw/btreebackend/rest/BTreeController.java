package com.dhbw.btreebackend.rest;

import com.dhbw.btreebackend.btreeimplementation.BTree;
import com.dhbw.btreebackend.btreeimplementation.BTreeSearchResult;
import com.dhbw.btreebackend.json.BTreeToJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@CrossOrigin
@RestController
@RequestMapping ("/api")
public class BTreeController {

    @Autowired
    private BTree bTree;

    /**
     * This method provides the endpoint for adding new values. It gets a list of new elements and calls the
     * getInsertedTreeRepresentationsAndInsertElements(), which inserts the elements to tree and creates a
     * JSON-representation of the tree for every insertion, to display the single steps in the frontend.
     *
     * @param newElements: The list of new elements, that will be added to the tree.
     * @return ResponseEntity, containing the JSON-List of the trees and Http status-code 200(Ok).
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addElements(@RequestBody List<Integer> newElements) {

        if (newElements == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Elemente nicht vollständig!");
        }

        return getInsertedTreeRepresentationsAndInsertElements(newElements);
    }

    /**
     * This method provides the endpoint for removing values from the tree. It gets a list of elements and removes
     * them iterative from the tree, while creating a JSON-representation for every step.
     *
     * @param deleteElements: The list of  elements, that will be removde from the tree.
     * @return ResponseEntity, containing the JSON-List of the trees and Http status-code 200(Ok).
     */
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteElements(@RequestBody List<Integer> deleteElements) {

        if (deleteElements == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Elemente nicht vollständig!");
        }
        List<JsonObject> answerTreeList = new ArrayList<JsonObject>();
        for (Integer i : deleteElements) {
            bTree.deleteElement(i);
            answerTreeList.add(BTreeToJson.createBTreeJson(bTree));
            System.out.println("Tree added");
        }
        System.out.println(answerTreeList);

        return new ResponseEntity<>(answerTreeList.toString(), HttpStatus.OK);
    }

    /**
     * This method provides the endpoint for searching for an element in the tree. It returns the costs of finding
     * the specific element and the uuid of the node, containing the element.
     *
     * @param searchElement: The element to be searched.
     * @return ResponseEntity, containing a JsonObject with costs, highlighted node and Http status-code 200(Ok).
     */
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> searchElement(@RequestBody Integer searchElement) {

        if (searchElement == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Element nicht vorhanden!");
        }
        BTreeSearchResult bTreeSearchResult = bTree.searchElement(searchElement);
        JsonObjectBuilder searchResultBuilder = Json.createObjectBuilder();
        if (bTreeSearchResult.isFound()) {
            searchResultBuilder.add("Highlighted", bTreeSearchResult.getLocation().getUuid().toString());
        } else {
            searchResultBuilder.add("Highlighted", JsonValue.NULL);
        }
        searchResultBuilder.add("Costs", bTreeSearchResult.getCosts());

        return new ResponseEntity<>(searchResultBuilder.build().toString(), HttpStatus.OK);
    }

    /**
     * This method provides the endpoint for adding random values. It gets a list of metrics for random elements and
     * calls getRandomMetrics, to create a list of numbers matching to the metrics (min, max and number of values.)
     * After that, it calls getInsertedTreeRepresentationsAndInsertElements(), which inserts the elements to tree and
     * creates a JSON-representation of the tree for every insertion, to display the single steps in the frontend.
     *
     * @param randomMetrics: The list of new elements, that will be added to the tree.
     * @return ResponseEntity, containing the JSON-List of the trees and Http status-code 200(Ok).
     */
    @PostMapping(value = "random", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> randomElements(@RequestBody List<Integer> randomMetrics) {

        if (randomMetrics == null || randomMetrics.size() != 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Element nicht vorhanden!");
        }

        List<Integer> valuesToAdd = this.getRandomMetrics(randomMetrics.get(0), randomMetrics.get(1), randomMetrics.get(2));

        return getInsertedTreeRepresentationsAndInsertElements(valuesToAdd);

    }

    /**
     * This method provides the endpoint for changing the order of the tree.
     * @param newOrder: The value of the new order.
     * @return ResponseEntity, containing the JSON of the new tree and Http status-code 200(Ok).
     */
    @PostMapping(value = "changeOrder", produces =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> changeOrder(@RequestBody int newOrder){

        if (newOrder == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ordnung 0 impossible");
        }
        // TODO Change Order of new Tree @Julian Stein

        List<JsonObject> answerTreeList = new ArrayList<JsonObject>();
        answerTreeList.add(BTreeToJson.createBTreeJson(bTree));

        return new ResponseEntity<>(answerTreeList.toString(), HttpStatus.OK);
    }

    /**
     * This method inserts the elements to tree and creates a JSON-representation of the tree for every insertion,
     * to display the single steps in the frontend.
     * @param valuesToAdd: The List of values to add to the tree.
     * @return ResponseEntity, containing the JSON of the new tree and Http status-code 200(Ok).
     */
    private ResponseEntity<Object> getInsertedTreeRepresentationsAndInsertElements(List<Integer> valuesToAdd) {

        List<JsonObject> answerTreeList = new ArrayList<JsonObject>();
        for (Integer i : valuesToAdd) {
            bTree.insertElement(i);
            answerTreeList.add(BTreeToJson.createBTreeJson(bTree));
            System.out.println("Tree added");
        }
        System.out.println(answerTreeList);

        return new ResponseEntity<>(answerTreeList.toString(), HttpStatus.OK);
    }

    /**
     * This method creates a list of random values based on the given input metrics.
     * @param min: The minimum value of the random values.
     * @param number: The number of random values.
     * @param max: The maximum value of the random values.
     * @return randomNumbers: The list containing the random numbers.
     */
    private List<Integer> getRandomMetrics(int min, int number, int max) {
        List<Integer> randomNumbers = new ArrayList<Integer>();
        for ( int i = 0; i < number; i++) {
            randomNumbers.add(ThreadLocalRandom.current().nextInt(min, max + 1));
        }
        return randomNumbers;
    }

}
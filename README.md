# BTreeBackend - SpringBootApplication

## Installation
To use this project work, you can use the b-tree-frontend provided in this [repository!](https://github.com/eliasmueller/b-tree-frontend) to visualize and command the BTree. Alternatively, you can attach your own framework compatible with RPCs described at the end of the README or just add other Java-Classes managing and manipulating the BTree.
In the following, the installation with the associated frontend application is explained.
The application is meant to be used locally, which is why the connection only works via localhost and if the to containers are running on the same machine.

First of all, you have to install a local [Docker environment](https://www.docker.com/get-started) to your machine, if you do not have one installed already.

Next, there are two ways to run the Docker containers.

1. You can create a directory on your machine called 'b-tree', and create a file called 'docker-compose.yml'. 
Into this docker compose file you have to paste the script below and save it.
```
version: '3.8'
 
services:
  b-tree-backend:
    image: steinju/b-tree-backend:latest
    container_name: b-tree-backend-container
    ports:
      - 8080:8080

  b-tree-frontend:
    image: eliasmueller/b-tree-frontend:latest
    container_name: b-tree-frontend-container
    ports: 
      - 4200:80
    depends_on:
      - b-tree-backend
```
Out of the newly created dir 'b-tree' run the following docker command:
```
docker-compose up
```

2. You can also run the two commands and fetch and run the Docker repositories yourself:
```
docker run -d -p 8080:8080 --name b-tree-backend-app-container steinju/b-tree-backend:latest
docker run -d -p 4200:80 --name b-tree-frontend-app-container eliasmueller/b-tree-frontend:latest
```
After running them, open a browser page at **localhost:4200** to open the application.

## Function Description Backend
The backend application contains the BTree's implementation and offers APIs for the frontend to execute common operations on the BTree (see 'Description of the RPCs' below).
As an abstraction the BTree's elements do not contain a full dataset but an integer key only. The BTree does not allow any duplicate keys.
For more in-depth descriptions of the algorithms please check the Javadoc comments in the source code.

### Description of the insert algorithm
When inserting new elements the insert position in a leaf node is searched for and the element will be added to it if the key does not exist already. Afterwards the tree checks whether the maximum number of elements in the node where the new element was added is exceeded (i.e. an overflow occured). If so, the tree splits the node with the overflow into two separate nodes moving the middle element to the parent as a separator for the splitted nodes. If no parent exists, a new root node is created. After moving the middle element to the parent, repeat the check for an overflow on the parent node and split the node if necessary. Repeat until a parent with no overflow is reached or a new root node was created.

### Description of the delete algorithm
When removing an element two cases are differentiated:
- Removal from a leaf node: Just remove the element and start balancing the tree starting at the leaf node.
- Removal from an internal node: Replace the element to remove with the greatest element in the left subtree, which is located in a leaf node. Remove the replacement from the leaf node and start balancing at that leaf node.

### Description of the balancing algorithm for an underflow
Rebalancing a node X: If X is the root node and has 0 elements left, the tree is now empty. Otherwise, if X is not the root node, determine based on the neighbours of X and the number of their elements what to do:
- X has left neighbour and left neighbour has more than the minimum number of elements: Perform a rightwards rotation moving the greatest element of the left neighbour to the parent node at the position of the element separating X and his left neighbour and moving the separating element to left edge of X. The tree is now balanced.
- X has no left neighbour but has a right neighbour containing more than the minimum number of elements: Perform a leftwards rotation moving the smallest element of the right neighbour to the parent node at the position of the element separating X and his left neighbour and moving the separating element to right edge of X. The tree is now balanced.
- No neighbour of X has more than the minimum number of elements: Merge X with its left neighbour (or right neighbour if X has no left neighbour) moving the element separating X and its neighbour in the parent element down to the node node resulting from the merge. If the parent is the root node and has zero elements left, set the merge result as the new root node. If the parent node is not the root node and now has less than the minimum number of elements, start balancing on the parent.

### Description of change order algorithm
When changing the order of the BTree the tree is rebuilt. After recursively retreiving all elements, the tree is reset and the order is changed. Next all former elements get inserted one by one.

### Description of the JSON-Parser for the BTree
The JSON-parser first counts the steps from root to leave, to get the height oft the tree.
After that, every node of the tree gets added to a list, which will be sorted in 'level-order'.
To make this work, first the root will be added to the 'previousRow'-list and to the whole 'levelOrderTree'-list.
Additionally, the last node of the tree (the last row's most right node) is got, to terminate the loop later on.
Then for every node in the previous row list, every element's children will be added to the 'currentRow'-list. 
Further, the 'currentRow'-list removes all dupcliates (which are added because children are stored redundant as 
left and right child of every element).
Last, the whole currentRow-List gets added to the 'levelOrderTree'-list and the 'currentRow' becomes the 'previousRow'.
This ends when the last node is added to the 'currentRow'-list.
After ordering the tree in 'level-order' a JSON-representation of the tree is created, by looping over the ordered nodes
and converting them to JSON-strings. Last the height of the tree, the number of the leaves and the order are added as
JSON-attributes

### Description of the RPCs
The RPC communication bases on plain http-calls. The backend endpoints consume and return specific data,
as defined in the API-definition below.

- Insert new elements.\
/api (POST)\
consumes: [int] (the positive integers representing the new elements to be added)\
returns: JSON (JSON consisting of two arrays representing the steps of adding the new elements: one array contains the actually inserted values as duplicates are ignored, the second array contains the states of the tree after each insertion)

- Remove elements.\
/api (DELETE)\
consumes: [int] (the positive integers representing the  elements to be removed)\
returns: JSON (JSON consisting of two arrays representing the steps of removing the elements: one array contains the actually removed values as values that do not exist cannot be removed, the second array contains the  states of the tree after each removal)

- Search for element.\
/api/search (POST)\
consumes: int (the element to search after)\
returns: {"Highlighted": UUID, "Costs": int} (json-object representing the highlighted node (where the element is) and the costs of searching the element)

- Add random elements.\
/api/random (POST)\
consumes: [int] (min, number, max: integers representing the metrics for adding new random elements)\
returns: JSON (JSON consisting of two arrays representing the steps of adding the new elements: one array contains the actually inserted values as duplicates are ignored, the second array contains the  states of the tree after each insertion)

- Change order of the tree.\
/api/changeOrder (POST)\
consumes: int\
returns: [JSON] (an array that represents the new tree with changed order)

- Reset the tree in the backend.\
/api/reset (POST)\
consumes: void\
returns: void

# BTreeBackend - SpringBootApplication

## Installation
To make this project work, you also need the b-tree-frontend provided in this [repository!](https://github.com/eliasmueller/b-tree-frontend)
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

### Description of the insert algorithm
When inserting new elements the insert position in a leaf node is searched for and the element will be added to it if the key does not exist already. Afterwards the tree checks whether the maximum number of elements in the node where the new element was added is exceeded (i.e. an overflow occured). If so, the tree splits the node with the overflow into two separate nodes moving the middle element to the parent as a separator for the splitted nodes. If no parent exists, a new root node is created. After moving the middle element to the parent repeat the check for an overflow on the parent node and split the node if necessary. Repeat until a parent with no overflow is reached or a new root node was created.

### Description of the delete algorithm.
When removing an element two cases are differentiated:
- Removal from a leaf node
- Removal from an internal node




### Description of the RPCs
The RPC communication bases on plain http-calls. The backend endpoints consume and return specific data,
as defined in the API-definition below.

Insert new elements.
/api (POST)
consumes: [int] (the positive integers representing the new elements to be added)
returns: [JSON] (an array that represents of the steps of adding the new elements)

Remove elements.
/api (DELETE)
consumes: [int] (the positive integers representing the  elements to be removed)
returns: [JSON] (an array that represents the steps of removing the elements)

Search for element.
/api/search (POST)
consumes: int (the element to search after)
returns: {"Highlighted": UUID, "Costs": int} (json-object representing the highlighted node (where the element is) and the costs of searching the element)

Add random elements.
/api/random (POST)
consumes: [int] (min, number, max: integers representing the metrics for adding new random elements)
returns: [JSON] (an array that represents the steps of removing the elements)

Change order of the tree.
/api/changeOrder (POST)
consumes: int
returns: [JSON] (an array that represents the new tree with changed order)

Reset the tree in the backend.
/api/reset (POST)
consumes: void
returns: void

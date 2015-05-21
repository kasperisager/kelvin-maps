Kelvin Maps
===========

Kelvin Maps is a mapping application for visualising and interacting with OpenStreetMap data. It was written by [@jhtr](https://github.itu.dk/jhtr), [@mgru](https://github.itu.dk/mgru), [@npel](https://github.itu.dk/npel), [@semb](https://github.itu.dk/semb), and [@kasi](https://github.itu.dk/kasi) as our first-year project at the [IT University of Copenhagen](http://itu.dk). If all else fails, we have at least succeeded in increasing the GDP of Colombia given our aggressive coffee consumption while writing this thing :coffee:

## Things of interest

### K-d trees

> Source: [`src/main/java/dk/itu/kelvin/util/PointTree.java`](src/main/java/dk/itu/kelvin/util/PointTree.java)

From Wikipedia:

> In computer science, a k-d tree (short for k-dimensional tree) is a space-partitioning data structure for organizing points in a k-dimensional space. k-d trees are a useful data structure for several applications, such as searches involving a multidimensional search key (e.g. range searches and nearest neighbor searches). k-d trees are a special case of binary space partitioning trees.

### R-trees

> Source: [`src/main/java/dk/itu/kelvin/util/RectangleTree.java`](src/main/java/dk/itu/kelvin/util/RectangleTree.java)

From Wikipedia:

> R-trees are tree data structures used for spatial access methods, i.e., for indexing multi-dimensional information such as geographical coordinates, rectangles or polygons. The R-tree was proposed by Antonin Guttman in 1984 and has found significant use in both theoretical and applied contexts. A common real-world usage for an R-tree might be to store spatial objects such as restaurant locations or the polygons that typical maps are made of: streets, buildings, outlines of lakes, coastlines, etc. and then find answers quickly to queries such as "Find all museums within 2 km of my current location", "retrieve all road segments within 2 km of my location" (to display them in a navigation system) or "find the nearest gas station" (although not taking roads into account). The R-tree can also accelerate nearest neighbor search for various distance metrics, including great-circle distance.

### Ternary search trees

> Source: [`src/main/java/dk/itu/kelvin/util/TernarySearchTree.java`](src/main/java/dk/itu/kelvin/util/TernarySearchTree.java)

From Wikipedia:

> In computer science, a ternary search tree is a type of tree (sometimes called a prefix tree) where nodes are arranged in a manner similar to a binary search tree, but with up to three children rather than the binary tree's limit of two. Like other prefix trees, a ternary search tree can be used as an associative map structure with the ability for incremental string search. However, ternary search trees are more space efficient compared to standard prefix trees, at the cost of speed. Common applications for ternary search trees include spell-checking and auto-completion.

### Dijkstra's algorithm

> Source: [`src/main/java/dk/itu/kelvin/util/ShortestPath.java`](src/main/java/dk/itu/kelvin/util/ShortestPath.java)

From Wikipedia:

> Dijkstra's algorithm is an algorithm for finding the shortest paths between nodes in a graph, which may represent, for example, road networks. It was conceived by computer scientist Edsger W. Dijkstra in 1956 and published three years later. The algorithm exists in many variants; Dijkstra's original variant found the shortest path between two nodes,[2] but a more common variant fixes a single node as the "source" node and finds shortest paths from the source to all other nodes in the graph, producing a shortest path tree.

---

Copyright &copy; 2015 [The Authors](https://github.itu.dk/gruppe-kelvin/kelvin-maps/graphs/contributors). Licensed under the terms of the [MIT license](LICENSE.md).

## FindSpanningTree

```
 FindSpanningTree(graph)
```

> find the minimum spanning tree in the `graph`.

```
 FindSpanningTree({graph, v})
```

> find the spanning tree grown from the vertex `v`.

As in Mathematica: a weighted undirected graph gives the minimum spanning forest (Kruskal, ties broken
by vertex position), an unweighted one a breadth-first tree, a weighted directed graph the minimum
arborescence or the lightest branching with the fewest roots, and an unweighted directed graph a
breadth-first branching. The tree keeps the graph's vertex order and its edges, sorted by position.
  
See  
* [Wikipedia - Minimum spanning tree](https://en.wikipedia.org/wiki/Minimum_spanning_tree)


### Examples

```
>> FindSpanningTree(Graph({a,b,c,d,e,f},{a<->b,a<->d,b<->c,b<->d,b<->e,c<->e,c<->f,d<->e,e<->f}, {EdgeWeight->{1.0,3.0,6.0,5.0,1.0,5.0,2.0,1.0,4.0}}))
Graph({a,b,c,d,e,f},{a<->b,b<->e,c<->f,d<->e,e<->f},{EdgeWeight->{1.0,1.0,2.0,1.0,4.0}})

>> EdgeList(FindSpanningTree(Graph({1,2,3,4}, {1->2, 2->3, 1->3, 3->4, 4->1}, EdgeWeight -> {5,1,2,3,4})))
{2->3,3->4,4->1}
```

### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindShortestTour](FindShortestTour.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindSpanningTree](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L1119) 

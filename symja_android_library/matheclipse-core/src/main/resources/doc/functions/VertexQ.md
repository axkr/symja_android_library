## VertexQ

```
VertexQ(graph, vertex)
```

> test if `vertex` is a vertex in the `graph` object.


See:
* [Wikipedia - Graph theory](https://en.wikipedia.org/wiki/Graph_theory)
 
### Examples

```
>> VertexQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),3) 
True

>> VertexQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),5) 
False
```

### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), 
[FindShortestTour](FindShortestTour.md), [FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), [VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of VertexQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L2140) 

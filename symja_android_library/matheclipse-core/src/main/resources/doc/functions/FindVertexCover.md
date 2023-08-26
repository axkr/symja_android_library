## FindVertexCover

```
 FindVertexCover(graph)
```

> algorithm to find a vertex cover for a `graph`. A vertex cover is a set of vertices that touches all the edges in the graph.

See  
* [Wikipedia - Vertex cover](https://en.wikipedia.org/wiki/Vertex_cover)


### Examples

```
>> FindVertexCover({1<->2,1<->3,2<->3,3<->4,3<->5,3<->6})
{3,1}
```

### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindShortestPath](FindShortestPath.md),[FindSpanningTree](FindSpanningTree.md), [FindShortestTour](FindShortestTour.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FindVertexCover](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L1781) 

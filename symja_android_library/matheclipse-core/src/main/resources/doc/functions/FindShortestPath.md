## FindShortestPath

```
 FindShortestPath(graph, source, destination)
```

> find a shortest path in the `graph` from `source` to `destination`.
 
See
* [Wikipedia - Pathfinding](https://en.wikipedia.org/wiki/Pathfinding)
* [Wikipedia - Shortest path problem](https://en.wikipedia.org/wiki/Shortest_path_problem)

### Examples

```
>> FindShortestPath(Graph({1 -> 2, 2 -> 4, 1 -> 3,  3 -> 2, 3 -> 4},{EdgeWeight->{3.0,1.0,1.0,1.0,3.0}}),1,4)
{1,3,2,4}
```

### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestTour](FindShortestTour.md), [FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FindShortestPath](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L1843) 

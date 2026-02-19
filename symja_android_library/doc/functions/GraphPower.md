## GraphPower

``` 
GraphPower(graph, n)
```

> the function uses Dijkstra's algorithm (i.e. [FindShortestPath](FindShortestPath.md)) to find the shortest path between each pair of vertices. If the length of the shortest path is less than or equal to `n`, it adds an edge between the vertices in the new graph. The result is a new graph that is the power of the original graph.
 
### Examples

```
>> GraphPower({1 -> 2, 2 -> 3, 3 -> 4, 4 -> 5, 5->6}, 2)
Graph({1,2,3,4,5,6},{1->2,1->3,2->3,2->4,3->4,3->5,4->5,4->6,5->6})
```

### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of GraphPower](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L735) 

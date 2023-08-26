## AdjacencyMatrix

``` 
AdjacencyMatrix(graph)
```

> convert the `graph` into a adjacency matrix in sparse array format.

See:
* [Wikipedia - Adjacency matrix](https://en.wikipedia.org/wiki/Adjacency_matrix)

### Examples

The sparse array can be converted to an adjacency matrix in `List` format with the `Normal` function:

```
>> AdjacencyMatrix(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2})) // Normal
{{0,1,1,0}, 
 {0,0,1,0}, 
 {0,0,0,0}, 
 {0,1,0,0}}
```

### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), [Normal](Normal.md), [VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md), [WeightedAdjacencyMatrix](WeightedAdjacencyMatrix.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AdjacencyMatrix](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L1031) 

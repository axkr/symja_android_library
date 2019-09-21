## AdjacencyMatrix

``` 
AdjacencyMatrix(graph)
```

> convert the `graph` into a adjacency matrix.

See:
* [Wikipedia - Adjacency matrix](https://en.wikipedia.org/wiki/Adjacency_matrix)

### Examples

```
>> AdjacencyMatrix(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2})) 
{{0,1,1,0}, 
 {0,0,1,0}, 
 {0,0,0,0}, 
 {0,1,0,0}}
```

### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 
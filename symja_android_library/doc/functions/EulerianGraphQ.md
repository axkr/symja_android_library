## EulerianGraphQ

```
EulerianGraphQ(graph)
```

> returns `True` if `graph` is an eulerian graph, and `False` otherwise.

See:
* [Wikipedia - Eulerian_pat](https://en.wikipedia.org/wiki/Eulerian_path)

### Examples

```
>> EulerianGraphQ(Graph({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 }))
True
```


### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 

 
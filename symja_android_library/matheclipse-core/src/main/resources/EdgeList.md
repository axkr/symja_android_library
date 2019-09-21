## EdgeList

``` 
EdgeList(graph)
```

> convert the `graph` into a list of edges. 

See:
* [Wikipedia - Graph theory](https://en.wikipedia.org/wiki/Graph_theory)

### Examples

```
>> EdgeList(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2})) 
{1->2,2->3,1->3,4->2}
```

### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), 
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 
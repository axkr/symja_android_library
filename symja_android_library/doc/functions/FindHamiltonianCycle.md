## FindHamiltonianCycle

```
 FindHamiltonianCycle(graph)
```

> find an hamiltonian cycle in the `graph`.
 
See
* [Wikipedia - Hamiltonian path](https://en.wikipedia.org/wiki/Hamiltonian_path)
* [Wikipedia - Hamiltonian path problem](https://en.wikipedia.org/wiki/Hamiltonian_path_problem)

### Examples

```
>> FindHamiltonianCycle( {1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1} )
{1->2,2->3,3->4,4->1}
```

### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md),
[FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindShortestTour](FindShortestTour.md), 
[FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FindHamiltonianCycle](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L1644) 

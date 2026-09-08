## FindHamiltonianCycle

```
FindHamiltonianCycle(graph)
```

> finds an hamiltonian cycle in the `graph` - a cycle visiting every vertex exactly once.

```
FindHamiltonianCycle(graph, k)
```

> finds at most `k` such cycles.

The result is a list of cycles, each a list of the edges walked in order, and `{}` when the graph has none. Only one cycle is produced, so `k` greater than 1 does not yield more; `FindHamiltonianCycle(graph, 1)` and `FindHamiltonianCycle(graph)` are the same.

A graph has an hamiltonian cycle exactly when [HamiltonianGraphQ](HamiltonianGraphQ.md) is `True`. Where [FindEulerianCycle](FindEulerianCycle.md) walks every *edge* once, this one visits every *vertex* once, so a cycle has one edge per vertex.

See
* [Wikipedia - Hamiltonian path](https://en.wikipedia.org/wiki/Hamiltonian_path)
* [Wikipedia - Hamiltonian path problem](https://en.wikipedia.org/wiki/Hamiltonian_path_problem)

### Examples

```
>> FindHamiltonianCycle( {1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1} )
{{1->2,2->3,3->4,4->1}}
```

The Petersen graph is the classic graph with no hamiltonian cycle:

```
>> FindHamiltonianCycle(PetersenGraph())
{}
```

### Related terms 
[FindEulerianCycle](FindEulerianCycle.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), [FindPostmanTour](FindPostmanTour.md), [FindShortestTour](FindShortestTour.md), [GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), 
[FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindHamiltonianCycle](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/builtin/GraphFunctions.java)

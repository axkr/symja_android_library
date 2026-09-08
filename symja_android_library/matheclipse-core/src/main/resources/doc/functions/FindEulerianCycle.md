## FindEulerianCycle

```
FindEulerianCycle(graph)
```

> finds an eulerian cycle in the `graph` - a cycle traversing every edge exactly once.

```
FindEulerianCycle(graph, k)
```

> finds at most `k` such cycles.

The result is a list of cycles, each a list of the edges walked in order, and `{}` when the graph has none. Only one cycle is produced, so `k` greater than 1 does not yield more; `FindEulerianCycle(graph, 1)` and `FindEulerianCycle(graph)` are the same.

A graph has an eulerian cycle exactly when [EulerianGraphQ](EulerianGraphQ.md) is `True`. When it has none, [FindPostmanTour](FindPostmanTour.md) still gives a shortest closed walk covering every edge, by repeating some of them.

See
* [Wikipedia - Eulerian path](https://en.wikipedia.org/wiki/Eulerian_path)


### Examples

```
>> FindEulerianCycle(Graph({1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1}))
{{4->1,1->2,2->3,3->4}}
```

Vertex 4 can be entered but never left, so there is no cycle:

```
>> FindEulerianCycle(Graph({1 -> 2, 2 -> 3, 3 -> 4, 3 -> 1}))
{}
```

### Related terms 
[FindPostmanTour](FindPostmanTour.md), [EulerianGraphQ](EulerianGraphQ.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindShortestTour](FindShortestTour.md), [GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindEulerianCycle](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/builtin/GraphFunctions.java)

## EdgeRules

``` 
EdgeRules(graph)
```

> convert the `graph` into a list of rules. All edge types (undirected, directed) are represented by a rule `lhs->rhs`.

See:
* [Wikipedia - Graph theory](https://en.wikipedia.org/wiki/Graph_theory)

### Examples

```
>> EdgeRules(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))
{1->2,2->3,3->1}
```

### Related terms 
[EdgeList](EdgeList.md), [GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), 
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of EdgeRules](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L1177) 

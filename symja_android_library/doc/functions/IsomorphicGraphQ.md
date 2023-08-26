## IsomorphicGraphQ

```
IsomorphicGraphQ(graph1, graph2)
```

> returns `True` if an isomorphism exists between `graph1` and `graph2`. Return `False`in all other cases.
 
See:
* [Wikipedia - Graph isomorphism](https://en.wikipedia.org/wiki/Graph_isomorphism) 
  

### Examples

```
>> IsomorphicGraphQ(Graph({1,2,3,4},{1<->2,1<->4,2<->3,3<->4}), Graph({1,2,3,4},{1<->3,1<->4,2<->3,2<->4}))
True
```

### Related terms 
[FindGraphIsomorphism](FindGraphIsomorphism.md), [GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md), [EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of IsomorphicGraphQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L1446) 

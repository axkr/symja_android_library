## WeightedGraphQ

``` 
WeightedGraphQ(expr)
```

> test if `expr` is an explicit weighted graph object.


See:
* [Wikipedia - Graph](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics))
* [Wikipedia - Graph theory](https://en.wikipedia.org/wiki/Graph_theory)
* [Wikipedia - Calculus on finite weighted graphs](https://en.wikipedia.org/wiki/Calculus_on_finite_weighted_graphs)

### Examples

```
>> WeightedGraphQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}) ) 
True
				
>> WeightedGraphQ( Sin(x) ) 
False

>> WeightedGraphQ( Graph({1->2, 2->3, 3->1}, EdgeWeight->{5.061,2.282,5.086}) )
True
```

### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md)
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindSpanningTree](FindSpanningTree.md), [Graph](Graph.md), [GraphQ](GraphQ.md),  [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of WeightedGraphQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L2220) 

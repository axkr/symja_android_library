## Graph

``` 
Graph({edge1,...,edgeN})
```

> create a graph from the given edges `edge1,...,edgeN`.


See:
* [Wikipedia - Graph](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics))
* [Wikipedia - Graph theory](https://en.wikipedia.org/wiki/Graph_theory)
 

### Examples

A directed graph:

```
>> Graph({1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1})  
```

An undirected graph:

```			
>> Graph({1 <-> 2, 2 <-> 3, 3 <-> 4, 4 <-> 1})   
```

An undirected weighted graph:

```			
>> Graph({1 <-> 2, 2 <-> 3, 3 <-> 4, 4 <-> 1},{EdgeWeight->{2.0,3.0,4.0, 5.0}})   
```

### Related terms 
[GraphCenter](GraphCenter.md), [GraphDiameter](GraphDiameter.md), [GraphPeriphery](GraphPeriphery.md), [GraphRadius](GraphRadius.md), [AdjacencyMatrix](AdjacencyMatrix.md), [EdgeList](EdgeList.md),
[EdgeQ](EdgeQ.md), [EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindVertexCover](FindVertexCover.md), [FindShortestPath](FindShortestPath.md), [FindSpanningTree](FindSpanningTree.md), [GraphQ](GraphQ.md), [HamiltonianGraphQ](HamiltonianGraphQ.md), 
[VertexEccentricity](VertexEccentricity.md), [VertexList](VertexList.md), [VertexQ](VertexQ.md) 
			

### Github

* [Implementation of Graph](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L185) 

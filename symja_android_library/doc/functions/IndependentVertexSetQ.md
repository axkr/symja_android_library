## IndependentVertexSetQ

```
IndependentVertexSetQ(graph, vertices)
```

> returns `True` if the list of `vertices` is an independent vertex set of `graph`, and `False` otherwise. An independent vertex set is a set of vertices of the graph no two of which are joined by an edge.

Edge directions are ignored, and a self loop does not keep its vertex out of an independent vertex set. The empty list is an independent vertex set of every graph.

See
* [Wikipedia - Independent set (graph theory)](https://en.wikipedia.org/wiki/Independent_set_(graph_theory))

### Examples

```
>> IndependentVertexSetQ(CycleGraph(6), {1,3,5})
True
```

The vertices `1` and `2` are joined by an edge:

```
>> IndependentVertexSetQ(CycleGraph(6), {1,2})
False
```

`FindIndependentVertexSet` returns a list of vertex sets:

```
>> IndependentVertexSetQ(PetersenGraph(), First(FindIndependentVertexSet(PetersenGraph())))
True
```

### Related terms
[FindIndependentVertexSet](FindIndependentVertexSet.md), [IndependentEdgeSetQ](IndependentEdgeSetQ.md), [VertexCoverQ](VertexCoverQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of IndependentVertexSetQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/IndependentVertexSetQ.java)

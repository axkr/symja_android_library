## EdgeCoverQ

```
EdgeCoverQ(graph, edges)
```

> returns `True` if the list of `edges` is an edge cover of `graph`, and `False` otherwise. An edge cover is a set of edges of the graph such that every vertex of the graph is incident to at least one of the edges.

See
* [Wikipedia - Edge cover](https://en.wikipedia.org/wiki/Edge_cover)

### Examples

```
>> EdgeCoverQ(PathGraph({1,2,3,4}), {1<->2,3<->4})
True
```

The vertex `4` is not covered:

```
>> EdgeCoverQ(PathGraph({1,2,3,4}), {1<->2,2<->3})
False
```

```
>> EdgeCoverQ(CycleGraph(4), FindEdgeCover(CycleGraph(4)))
True
```

### Related terms
[FindEdgeCover](FindEdgeCover.md), [FindVertexCover](FindVertexCover.md), [IndependentEdgeSetQ](IndependentEdgeSetQ.md), [VertexCoverQ](VertexCoverQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of EdgeCoverQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/EdgeCoverQ.java)

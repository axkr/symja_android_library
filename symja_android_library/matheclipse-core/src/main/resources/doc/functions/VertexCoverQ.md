## VertexCoverQ

```
VertexCoverQ(graph, vertices)
```

> returns `True` if the list of `vertices` is a vertex cover of `graph`, and `False` otherwise. A vertex cover is a set of vertices of the graph such that every edge of the graph has at least one of its endpoints in the set.

Edge directions are ignored. A set of vertices is a vertex cover exactly if the remaining vertices form an independent vertex set.

See
* [Wikipedia - Vertex cover](https://en.wikipedia.org/wiki/Vertex_cover)

### Examples

```
>> VertexCoverQ(CycleGraph(4), {1,3})
True
```

The edge `3<->4` is not covered:

```
>> VertexCoverQ(CycleGraph(4), {1,2})
False
```

The center of a star covers all edges:

```
>> VertexCoverQ(StarGraph(5), {1})
True

>> VertexCoverQ(PetersenGraph(), FindVertexCover(PetersenGraph()))
True
```

### Related terms
[EdgeCoverQ](EdgeCoverQ.md), [FindVertexCover](FindVertexCover.md), [IndependentEdgeSetQ](IndependentEdgeSetQ.md), [IndependentVertexSetQ](IndependentVertexSetQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of VertexCoverQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/VertexCoverQ.java)

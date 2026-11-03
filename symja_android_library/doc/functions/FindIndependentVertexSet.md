## FindIndependentVertexSet

```
FindIndependentVertexSet(graph)
```

> finds an independent vertex set of `graph` with the largest possible number of vertices. In an independent vertex set no two vertices are joined by an edge.

```
FindIndependentVertexSet(graph, n)
```

> finds a largest independent vertex set with at most `n` vertices.

```
FindIndependentVertexSet(graph, {n})
```

> finds an independent vertex set with exactly `n` vertices.

```
FindIndependentVertexSet(graph, {min, max})
```

> finds a largest independent vertex set with between `min` and `max` vertices.

```
FindIndependentVertexSet(graph, nspec, s)
```

> finds at most `s` independent vertex sets. With `s` set to `All`, all of them are returned.

The result is a list of vertex sets, and the empty list `{}` if no set of the requested size exists. Edge directions and edge weights are ignored.

See
* [Wikipedia - Independent set (graph theory)](https://en.wikipedia.org/wiki/Independent_set_(graph_theory))

### Examples

```
>> FindIndependentVertexSet(CycleGraph(6))
{{1,3,5}}

>> FindIndependentVertexSet(StarGraph(5))
{{2,3,4,5}}

>> FindIndependentVertexSet(PetersenGraph())
{{1,2,8,10}}
```

All independent vertex sets with exactly `2` vertices of a cycle of length `5`:

```
>> FindIndependentVertexSet(CycleGraph(5), {2}, All)
{{1,3},{1,4},{2,4},{2,5},{3,5}}
```

### Related terms
[FindClique](FindClique.md), [FindIndependentEdgeSet](FindIndependentEdgeSet.md), [FindVertexCover](FindVertexCover.md), [Graph](Graph.md), [IndependentVertexSetQ](IndependentVertexSetQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindIndependentVertexSet](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindIndependentVertexSet.java)

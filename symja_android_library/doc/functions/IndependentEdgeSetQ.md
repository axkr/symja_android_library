## IndependentEdgeSetQ

```
IndependentEdgeSetQ(graph, edges)
```

> returns `True` if the list of `edges` is an independent edge set of `graph`, and `False` otherwise. An independent edge set, also known as a matching, is a set of edges of the graph no two of which are incident to the same vertex.

A self loop meets its own vertex twice and is never part of an independent edge set. The empty list is an independent edge set of every graph.

See
* [Wikipedia - Matching (graph theory)](https://en.wikipedia.org/wiki/Matching_(graph_theory))

### Examples

```
>> IndependentEdgeSetQ(PathGraph({1,2,3,4}), {1<->2,3<->4})
True
```

Both edges are incident to the vertex `2`:

```
>> IndependentEdgeSetQ(PathGraph({1,2,3,4}), {1<->2,2<->3})
False
```

```
>> IndependentEdgeSetQ(PetersenGraph(), FindIndependentEdgeSet(PetersenGraph()))
True
```

### Related terms
[EdgeCoverQ](EdgeCoverQ.md), [FindIndependentEdgeSet](FindIndependentEdgeSet.md), [IndependentVertexSetQ](IndependentVertexSetQ.md), [VertexCoverQ](VertexCoverQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of IndependentEdgeSetQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/IndependentEdgeSetQ.java)

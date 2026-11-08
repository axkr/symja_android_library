## FindEdgeCover

```
FindEdgeCover(graph)
```

> finds an edge cover of `graph` with the smallest possible number of edges. An edge cover is a set of edges such that every vertex of the graph is incident to at least one of the edges.

The edges are returned in the order of `EdgeList(graph)`. If a vertex of `graph` has no edge, no edge cover exists and the result is the empty list `{}`. Edge directions and edge weights are ignored.

See
* [Wikipedia - Edge cover](https://en.wikipedia.org/wiki/Edge_cover)

### Examples

```
>> FindEdgeCover(PathGraph({1,2,3,4,5}))
{1<->2,3<->4,4<->5}

>> FindEdgeCover(StarGraph(5))
{1<->2,1<->3,1<->4,1<->5}
```

A directed graph gives directed edges:

```
>> FindEdgeCover(Graph({1->2,2->3,3->4}))
{1->2,3->4}
```

The vertex `3` has no edge:

```
>> FindEdgeCover(Graph({1,2,3},{1<->2}))
{}
```

### Related terms
[EdgeCoverQ](EdgeCoverQ.md), [FindIndependentEdgeSet](FindIndependentEdgeSet.md), [FindVertexCover](FindVertexCover.md), [Graph](Graph.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindEdgeCover](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindEdgeCover.java)

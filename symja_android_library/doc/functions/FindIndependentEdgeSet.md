## FindIndependentEdgeSet

```
FindIndependentEdgeSet(graph)
```

> finds an independent edge set of `graph` with the largest possible number of edges, also known as a maximum matching. In an independent edge set no two edges are incident to the same vertex.

The edges are returned in the order of `EdgeList(graph)`. Edge directions and edge weights are ignored, and a self loop is never part of an independent edge set.

See
* [Wikipedia - Matching (graph theory)](https://en.wikipedia.org/wiki/Matching_(graph_theory))

### Examples

```
>> FindIndependentEdgeSet(PathGraph({1,2,3,4,5}))
{1<->2,3<->4}

>> FindIndependentEdgeSet(CompleteGraph(4))
{1<->2,3<->4}
```

The Petersen graph has a perfect matching, which touches every vertex:

```
>> FindIndependentEdgeSet(PetersenGraph())
{1<->3,2<->4,5<->10,6<->7,8<->9}
```

A directed graph gives directed edges:

```
>> FindIndependentEdgeSet(Graph({1->2,2->3,3->4}))
{1->2,3->4}
```

### Related terms
[FindEdgeCover](FindEdgeCover.md), [FindIndependentVertexSet](FindIndependentVertexSet.md), [Graph](Graph.md), [IndependentEdgeSetQ](IndependentEdgeSetQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindIndependentEdgeSet](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindIndependentEdgeSet.java)

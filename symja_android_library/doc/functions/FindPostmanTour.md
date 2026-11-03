## FindPostmanTour

```
FindPostmanTour(graph)
```

> finds a Chinese postman tour of `graph`: a shortest closed walk which traverses every edge of `graph` at least once.

```
FindPostmanTour(graph, k)
```

> finds at most `k` Chinese postman tours.

The result is a list of tours, each given as the list of the edges in the order they are walked. If all vertices of the graph have an even degree, the graph is Eulerian and the tour walks every edge exactly once. Otherwise some edges are walked twice. A tour exists only if all edges belong to one connected component, which must be strongly connected for a directed graph; otherwise the result is the empty list `{}`. Vertices without edges are ignored.

See
* [Wikipedia - Chinese postman problem](https://en.wikipedia.org/wiki/Chinese_postman_problem)

### Examples

A cycle is Eulerian:

```
>> FindPostmanTour(CycleGraph(4))
{{3->4,4->1,1->2,2->3}}
```

In a path or a star every edge is walked twice:

```
>> FindPostmanTour(PathGraph({1,2,3}))
{{3->2,2->1,1->2,2->3}}

>> FindPostmanTour(StarGraph(4))
{{4->1,1->3,3->1,1->2,2->1,1->4}}
```

All ten vertices of the Petersen graph have the degree `3`, so `5` of its `15` edges are walked twice:

```
>> Length(FindPostmanTour(PetersenGraph())[[1]])
20
```

The edges of a graph with two components cannot be walked in one tour:

```
>> FindPostmanTour(Graph({1,2,3,4},{1<->2,3<->4}))
{}
```

### Related terms
[EulerianGraphQ](EulerianGraphQ.md), [FindEulerianCycle](FindEulerianCycle.md), [FindHamiltonianCycle](FindHamiltonianCycle.md), [FindShortestTour](FindShortestTour.md), [Graph](Graph.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindPostmanTour](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindPostmanTour.java)

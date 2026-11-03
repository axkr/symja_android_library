## FindClique

```
FindClique(graph)
```

> finds a largest clique of `graph`. A clique is a set of vertices in which every two vertices are joined by an edge.

```
FindClique(graph, n)
```

> finds a largest clique with at most `n` vertices.

```
FindClique(graph, {n})
```

> finds a clique with exactly `n` vertices.

```
FindClique(graph, {min, max})
```

> finds a largest clique with between `min` and `max` vertices.

```
FindClique(graph, nspec, s)
```

> finds at most `s` cliques. With `s` set to `All`, all cliques are returned.

The result is a list of cliques, each given as a list of vertices, and the empty list `{}` if no clique of the requested size exists. Edge directions and edge weights are ignored.

See
* [Wikipedia - Clique (graph theory)](https://en.wikipedia.org/wiki/Clique_(graph_theory))

### Examples

```
>> FindClique({1<->2,2<->3,3<->1,3<->4,4<->5,5<->3,5<->6})
{{1,2,3}}

>> FindClique(CompleteGraph(5))
{{1,2,3,4,5}}
```

All maximal cliques of the graph, the largest first:

```
>> FindClique({1<->2,2<->3,3<->1,3<->4,4<->5,5<->3,5<->6}, Infinity, All)
{{1,2,3},{3,4,5},{5,6}}
```

All cliques with exactly `2` vertices:

```
>> FindClique({1<->2,2<->3,3<->1,3<->4,4<->5,5<->3,5<->6}, {2}, All)
{{1,2},{3,4},{5,6}}
```

A cycle of length `5` contains no triangle:

```
>> FindClique(CycleGraph(5), {3})
{}
```

### Related terms
[FindIndependentVertexSet](FindIndependentVertexSet.md), [FindKClan](FindKClan.md), [FindKClique](FindKClique.md), [FindKClub](FindKClub.md), [FindKPlex](FindKPlex.md), [Graph](Graph.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindClique](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindClique.java)

## FindKClan

```
FindKClan(graph, k)
```

> finds a largest k-clan of `graph`. A k-clan is a k-clique whose induced subgraph has a diameter of at most `k`.

```
FindKClan(graph, k, n)
```

> finds a largest k-clan with at most `n` vertices.

```
FindKClan(graph, k, {n})
```

> finds a k-clan with exactly `n` vertices.

```
FindKClan(graph, k, {min, max})
```

> finds a largest k-clan with between `min` and `max` vertices.

```
FindKClan(graph, k, nspec, s)
```

> finds at most `s` k-clans. With `s` set to `All`, all of them are returned.

The members of a k-clique only have to be at a distance of at most `k` in the whole graph. The members of a k-clan must reach each other within `k` steps along edges between members of the set. Every k-clan is a k-clique, and `FindKClan(graph, 1)` gives the same result as `FindClique(graph)`.

See
* [Wikipedia - Clique (graph theory)](https://en.wikipedia.org/wiki/Clique_(graph_theory))

### Examples

```
>> FindKClan(CycleGraph(5), 2)
{{1,2,3,4,5}}

>> FindKClan(CycleGraph(6), 2)
{{1,2,3}}

>> FindKClan({1<->2,2<->3,3<->4,4<->5,5<->1,1<->6}, 2, Infinity, All)
{{1,2,3,4,5},{1,2,5,6}}
```

In a cycle of length `6` the set `{1,3,5}` is a 2-clique, but not a 2-clan, because no two of its vertices are joined by an edge:

```
>> FindKClan(CycleGraph(6), 2, {3}, All)
{{1,2,3},{1,2,6},{1,5,6},{2,3,4},{3,4,5},{4,5,6}}
```

### Related terms
[FindClique](FindClique.md), [FindKClique](FindKClique.md), [FindKClub](FindKClub.md), [FindKPlex](FindKPlex.md), [Graph](Graph.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindKClan](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindKClan.java)

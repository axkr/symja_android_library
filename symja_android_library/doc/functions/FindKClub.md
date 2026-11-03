## FindKClub

```
FindKClub(graph, k)
```

> finds a largest k-club of `graph`. A k-club is a maximal set of vertices whose induced subgraph has a diameter of at most `k`.

```
FindKClub(graph, k, n)
```

> finds a largest k-club with at most `n` vertices.

```
FindKClub(graph, k, {n})
```

> finds a k-club with exactly `n` vertices.

```
FindKClub(graph, k, {min, max})
```

> finds a largest k-club with between `min` and `max` vertices.

```
FindKClub(graph, k, nspec, s)
```

> finds at most `s` k-clubs. With `s` set to `All`, all of them are returned.

The members of a k-club must reach each other within `k` steps along edges between members of the set. Every k-clan is a k-club, and `FindKClub(graph, 1)` gives the same result as `FindClique(graph)`. Finding several k-clubs takes much longer than finding one.

See
* [Wikipedia - Clique (graph theory)](https://en.wikipedia.org/wiki/Clique_(graph_theory))

### Examples

```
>> FindKClub(CycleGraph(6), 2)
{{1,2,3}}

>> FindKClub(CycleGraph(8), 3)
{{1,2,3,4}}
```

In a cycle of length `6` the set `{1,3,5}` is a 2-clique, but not a 2-club, because no two of its vertices are joined by an edge:

```
>> FindKClub(CycleGraph(6), 2, {3}, All)
{{1,2,3},{1,2,6},{1,5,6},{2,3,4},{3,4,5},{4,5,6}}
```

### Related terms
[FindClique](FindClique.md), [FindKClan](FindKClan.md), [FindKClique](FindKClique.md), [FindKPlex](FindKPlex.md), [Graph](Graph.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindKClub](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindKClub.java)

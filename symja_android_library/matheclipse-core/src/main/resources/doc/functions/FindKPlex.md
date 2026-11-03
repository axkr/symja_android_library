## FindKPlex

```
FindKPlex(graph, k)
```

> finds a largest k-plex of `graph`. A k-plex is a maximal set of vertices in which every vertex is adjacent to all but at most `k` of the members, counting the vertex itself.

```
FindKPlex(graph, k, n)
```

> finds a largest k-plex with at most `n` vertices.

```
FindKPlex(graph, k, {n})
```

> finds a k-plex with exactly `n` vertices.

```
FindKPlex(graph, k, {min, max})
```

> finds a largest k-plex with between `min` and `max` vertices.

```
FindKPlex(graph, k, nspec, s)
```

> finds at most `s` k-plexes. With `s` set to `All`, all of them are returned.

In a k-plex with `m` vertices every member has at least `m-k` neighbors inside the set. A 1-plex is a clique, so `FindKPlex(graph, 1)` gives the same result as `FindClique(graph)`. Finding several k-plexes takes much longer than finding one.

See
* [Wikipedia - Clique (graph theory)](https://en.wikipedia.org/wiki/Clique_(graph_theory))

### Examples

```
>> FindKPlex(CycleGraph(5), 1)
{{1,2}}

>> FindKPlex(CycleGraph(5), 2)
{{1,2,3}}

>> FindKPlex(CompleteGraph(5), 2)
{{1,2,3,4,5}}
```

All 2-plexes with `3` vertices of a cycle of length `6`:

```
>> FindKPlex(CycleGraph(6), 2, {3}, All)
{{1,2,3},{1,2,6},{1,5,6},{2,3,4},{3,4,5},{4,5,6}}
```

### Related terms
[FindClique](FindClique.md), [FindKClan](FindKClan.md), [FindKClique](FindKClique.md), [FindKClub](FindKClub.md), [Graph](Graph.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindKPlex](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindKPlex.java)

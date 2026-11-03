## FindKClique

```
FindKClique(graph, k)
```

> finds a largest k-clique of `graph`. A k-clique is a maximal set of vertices which are at a distance of at most `k` from each other in `graph`.

```
FindKClique(graph, k, n)
```

> finds a largest k-clique with at most `n` vertices.

```
FindKClique(graph, k, {n})
```

> finds a k-clique with exactly `n` vertices.

```
FindKClique(graph, k, {min, max})
```

> finds a largest k-clique with between `min` and `max` vertices.

```
FindKClique(graph, k, nspec, s)
```

> finds at most `s` k-cliques. With `s` set to `All`, all of them are returned.

The shortest paths between the members of a k-clique may run through vertices outside of the set. A 1-clique is a clique, so `FindKClique(graph, 1)` gives the same result as `FindClique(graph)`.

See
* [Wikipedia - Clique (graph theory)](https://en.wikipedia.org/wiki/Clique_(graph_theory))

### Examples

```
>> FindKClique(CycleGraph(6), 2)
{{1,2,3}}

>> FindKClique(PathGraph({1,2,3,4,5}), 2, Infinity, All)
{{1,2,3},{2,3,4},{3,4,5}}
```

In a cycle of length `6` the vertices `1`, `3` and `5` are at the distance `2` from each other, so `{1,3,5}` is a 2-clique, although no two of these vertices are joined by an edge:

```
>> FindKClique(CycleGraph(6), 2, {3}, All)
{{1,2,3},{1,2,6},{1,3,5},{1,5,6},{2,3,4},{2,4,6},{3,4,5},{4,5,6}}
```

### Related terms
[FindClique](FindClique.md), [FindKClan](FindKClan.md), [FindKClub](FindKClub.md), [FindKPlex](FindKPlex.md), [Graph](Graph.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindKClique](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindKClique.java)

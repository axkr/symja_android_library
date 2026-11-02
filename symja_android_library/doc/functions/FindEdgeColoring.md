## FindEdgeColoring

```
FindEdgeColoring(graph)
```

> finds a coloring of the edges of `graph` with the smallest possible number of colors, so that no two edges sharing a vertex have the same color. The colors are the integers `1, 2, 3, ...`, given in the order of `EdgeList(graph)`.

```
FindEdgeColoring(graph, l)
```

> finds an edge coloring which uses only the colors `1, 2, ..., l`.

```
FindEdgeColoring(graph, {c1, c2, ...})
```

> finds an edge coloring which uses only the colors `c1, c2, ...`.

If no coloring with the given colors exists, the expression stays unevaluated.

See
* [Wikipedia - Edge coloring](https://en.wikipedia.org/wiki/Edge_coloring)

### Examples

An odd cycle needs `3` colors. The colors belong to the edges in the order of `EdgeList`:

```
>> FindEdgeColoring(CycleGraph(5))
{1,2,2,1,3}

>> EdgeList(CycleGraph(5))
{1<->2,1<->5,2<->3,3<->4,4<->5}
```

All edges of a star meet at the center, so each of them gets its own color:

```
>> FindEdgeColoring(StarGraph(4))
{1,2,3}
```

Use the given colors:

```
>> FindEdgeColoring(CycleGraph(4), {a, b})
{a,b,b,a}
```

An odd cycle cannot be colored with `2` colors:

```
>> FindEdgeColoring(CycleGraph(5), 2)
FindEdgeColoring(Graph({1,2,3,4,5},{1<->2,1<->5,2<->3,3<->4,4<->5}),2)
```

### Related terms
[EdgeChromaticNumber](EdgeChromaticNumber.md), [EdgeList](EdgeList.md), [FindPlanarColoring](FindPlanarColoring.md), [FindVertexColoring](FindVertexColoring.md), [Graph](Graph.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindEdgeColoring](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindEdgeColoring.java)

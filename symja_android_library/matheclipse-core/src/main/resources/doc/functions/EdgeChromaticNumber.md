## EdgeChromaticNumber

```
EdgeChromaticNumber(graph)
```

> returns the chromatic index of `graph`: the smallest number of colors needed to color the edges of `graph` so that no two edges sharing a vertex have the same color.

By Vizing's theorem the chromatic index of a simple graph with the maximum vertex degree `d` is either `d` or `d+1`.

See
* [Wikipedia - Edge coloring](https://en.wikipedia.org/wiki/Edge_coloring)

### Examples

An odd cycle needs `3` colors:

```
>> EdgeChromaticNumber(CycleGraph(5))
3
```

A complete graph with an even number `n` of vertices needs `n-1` colors, with an odd number `n` of vertices `n` colors:

```
>> EdgeChromaticNumber(CompleteGraph(6))
5

>> EdgeChromaticNumber(CompleteGraph(5))
5
```

The Petersen graph has the maximum degree `3`, but needs `4` colors:

```
>> EdgeChromaticNumber(PetersenGraph())
4
```

The graph can also be given as a list of edges:

```
>> EdgeChromaticNumber({1<->2,2<->3,3<->4})
2
```

### Related terms
[FindEdgeColoring](FindEdgeColoring.md), [FindVertexColoring](FindVertexColoring.md), [Graph](Graph.md), [VertexChromaticNumber](VertexChromaticNumber.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of EdgeChromaticNumber](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/EdgeChromaticNumber.java)

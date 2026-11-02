## VertexChromaticNumber

```
VertexChromaticNumber(graph)
```

> returns the chromatic number of `graph`: the smallest number of colors needed to color the vertices of `graph` so that no two vertices joined by an edge have the same color.

It is the number of different colors in `FindVertexColoring(graph)`.

See
* [Wikipedia - Graph coloring](https://en.wikipedia.org/wiki/Graph_coloring)

### Examples

```
>> VertexChromaticNumber(CycleGraph(5))
3

>> VertexChromaticNumber(PetersenGraph())
3

>> VertexChromaticNumber(CompleteGraph(7))
7
```

A grid graph is bipartite:

```
>> VertexChromaticNumber(GridGraph({3,3}))
2
```

### Related terms
[BipartiteGraphQ](BipartiteGraphQ.md), [EdgeChromaticNumber](EdgeChromaticNumber.md), [FindVertexColoring](FindVertexColoring.md), [Graph](Graph.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of VertexChromaticNumber](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/VertexChromaticNumber.java)

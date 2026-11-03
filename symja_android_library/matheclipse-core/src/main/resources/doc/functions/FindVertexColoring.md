## FindVertexColoring

```
FindVertexColoring(graph)
```

> finds a coloring of the vertices of `graph` with the smallest possible number of colors, so that no two vertices joined by an edge have the same color. The colors are the integers `1, 2, 3, ...`, given in the order of `VertexList(graph)`.

```
FindVertexColoring(graph, l)
```

> finds a vertex coloring which uses only the colors `1, 2, ..., l`.

```
FindVertexColoring(graph, {c1, c2, ...})
```

> finds a vertex coloring which uses only the colors `c1, c2, ...`.

If no coloring with the given colors exists, the expression stays unevaluated.

The option `Method` selects the algorithm:

* `Method -> Automatic` (the default) searches for a coloring with the smallest number of colors. With the option `PerformanceGoal -> "Speed"` it uses the faster DSatur heuristic instead, which may need more colors.
* `Method -> "ILP"` and `Method -> "BacktrackingDS"` always search for a coloring with the smallest number of colors.
* `Method -> "HybridEA"` uses the DSatur heuristic.
* `Method -> "BrownBacktrack"`, `"Greedy"`, `"SaturationDegree"`, `"DSatur"`, `"LargestDegreeFirst"`, `"SmallestDegreeLast"`, `"RandomGreedy"` or `"Chordal"` use the vertex coloring algorithms of the JGraphT library.

See
* [Wikipedia - Graph coloring](https://en.wikipedia.org/wiki/Graph_coloring)

### Examples

An odd cycle needs `3` colors:

```
>> FindVertexColoring(CycleGraph(5))
{1,2,1,2,3}

>> FindVertexColoring(CompleteGraph(4))
{1,2,3,4}

>> FindVertexColoring(PetersenGraph())
{1,1,2,2,3,2,3,1,3,1}
```

Use the given colors:

```
>> FindVertexColoring(CycleGraph(5), {a, b, c})
{a,b,a,b,c}
```

An odd cycle cannot be colored with `2` colors:

```
>> FindVertexColoring(CycleGraph(5), 2)
FindVertexColoring(Graph({1,2,3,4,5},{1<->2,1<->5,2<->3,3<->4,4<->5}),2)
```

The `"DSatur"` algorithm of JGraphT:

```
>> FindVertexColoring(PetersenGraph(), Method->"DSatur")
{1,2,3,3,1,2,3,1,2,3}
```

### Related terms
[EdgeChromaticNumber](EdgeChromaticNumber.md), [FindEdgeColoring](FindEdgeColoring.md), [FindPlanarColoring](FindPlanarColoring.md), [Graph](Graph.md), [VertexChromaticNumber](VertexChromaticNumber.md), [VertexList](VertexList.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindVertexColoring](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindVertexColoring.java)

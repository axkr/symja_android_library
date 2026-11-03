## FindPlanarColoring

```
FindPlanarColoring(graph)
```

> finds a coloring of the faces of the planar `graph` with the smallest possible number of colors, so that no two faces sharing an edge have the same color. The colors are the integers `1, 2, 3, ...`, given in the order of `PlanarFaceList(graph)`.

```
FindPlanarColoring(graph, l)
```

> finds a face coloring which uses only the colors `1, 2, ..., l`.

```
FindPlanarColoring(graph, {c1, c2, ...})
```

> finds a face coloring which uses only the colors `c1, c2, ...`.

By the four color theorem at most `4` colors are needed. If no coloring with the given colors exists, or if `graph` is not planar, the expression stays unevaluated.

See
* [Wikipedia - Four color theorem](https://en.wikipedia.org/wiki/Four_color_theorem)

### Examples

All four faces of the tetrahedron touch each other:

```
>> FindPlanarColoring(CompleteGraph(4))
{1,2,3,4}
```

The opposite faces of the cube get the same color:

```
>> FindPlanarColoring(HypercubeGraph(3))
{1,2,3,3,1,2}

>> PlanarFaceList(HypercubeGraph(3))
{{1,2,6,5},{2,1,3,4},{3,1,5,7},{2,4,8,6},{4,3,7,8},{5,6,8,7}}
```

Use the given colors:

```
>> FindPlanarColoring(CompleteGraph(4), {a,b,c,d})
{a,b,c,d}
```

The faces of the tetrahedron cannot be colored with `3` colors:

```
>> FindPlanarColoring(CompleteGraph(4), 3)
FindPlanarColoring(Graph({1,2,3,4},{1<->2,1<->3,1<->4,2<->3,2<->4,3<->4}),3)
```

### Related terms
[DualPlanarGraph](DualPlanarGraph.md), [FindEdgeColoring](FindEdgeColoring.md), [FindVertexColoring](FindVertexColoring.md), [PlanarFaceList](PlanarFaceList.md), [PlanarGraphQ](PlanarGraphQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindPlanarColoring](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/FindPlanarColoring.java)

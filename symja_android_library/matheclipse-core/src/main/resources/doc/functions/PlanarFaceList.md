## PlanarFaceList

```
PlanarFaceList(graph)
```

> returns the faces of the planar `graph`. Each face is given as the list of the vertices on its boundary, in the order in which the boundary is walked.

The outer face comes first. It is left out with the option `IncludeOuterFace -> False`. A connected planar graph with `n` vertices and `m` edges has `m-n+2` faces. The boundary of a face which contains a bridge visits a vertex more than once. If `graph` is not planar, the expression stays unevaluated.

See
* [Wikipedia - Planar graph](https://en.wikipedia.org/wiki/Planar_graph)

### Examples

The four triangles of the tetrahedron:

```
>> PlanarFaceList(CompleteGraph(4))
{{1,2,4},{2,1,3},{3,1,4},{2,3,4}}
```

The outer face of a wheel is its rim:

```
>> PlanarFaceList(WheelGraph(5))
{{2,3,4,5},{1,2,5},{2,1,3},{3,1,4},{4,1,5}}

>> PlanarFaceList(WheelGraph(5), IncludeOuterFace->False)
{{1,2,5},{2,1,3},{3,1,4},{4,1,5}}
```

A path has only the outer face, whose boundary walks along both sides of every edge:

```
>> PlanarFaceList(PathGraph({1,2,3}))
{{1,2,3,2}}
```

`CompleteGraph(5)` is not planar:

```
>> PlanarFaceList(CompleteGraph(5))
PlanarFaceList(Graph({1,2,3,4,5},{1<->2,1<->3,1<->4,1<->5,2<->3,2<->4,2<->5,3<->4,3<->5,4<->5}))
```

### Related terms
[DualPlanarGraph](DualPlanarGraph.md), [FindPlanarColoring](FindPlanarColoring.md), [Graph](Graph.md), [PlanarGraphQ](PlanarGraphQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of PlanarFaceList](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/PlanarFaceList.java)

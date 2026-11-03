## DualPlanarGraph

```
DualPlanarGraph(graph)
```

> returns the dual graph of the planar `graph`. The dual has one vertex for every face of `graph` and an edge between two faces which are separated by an edge of `graph`.

The vertices of the dual are the integers `1, 2, ..., k`, numbered in the order in which `PlanarFaceList(graph)` lists the faces. The dual is a simple graph: two faces sharing several edges are joined only once, and a bridge, which has the same face on both sides, adds no self loop. If `graph` is not planar, the expression stays unevaluated.

See
* [Wikipedia - Dual graph](https://en.wikipedia.org/wiki/Dual_graph)

### Examples

The tetrahedron is self-dual:

```
>> EdgeList(DualPlanarGraph(CompleteGraph(4)))
{1<->2,1<->3,1<->4,2<->3,2<->4,3<->4}
```

The cube has `6` faces, so its dual, the octahedron, has `6` vertices:

```
>> VertexCount(DualPlanarGraph(HypercubeGraph(3)))
6
```

A cycle separates an inner and an outer face:

```
>> EdgeList(DualPlanarGraph(CycleGraph(5)))
{1<->2}
```

Vertex `1` of the dual is the outer face of the grid, which borders all four squares:

```
>> EdgeList(DualPlanarGraph(GridGraph({3,3})))
{1<->2,1<->3,1<->4,1<->5,2<->3,2<->4,3<->5,4<->5}
```

`CompleteGraph(5)` is not planar:

```
>> DualPlanarGraph(CompleteGraph(5))
DualPlanarGraph(Graph({1,2,3,4,5},{1<->2,1<->3,1<->4,1<->5,2<->3,2<->4,2<->5,3<->4,3<->5,4<->5}))
```

### Related terms
[FindPlanarColoring](FindPlanarColoring.md), [Graph](Graph.md), [PlanarFaceList](PlanarFaceList.md), [PlanarGraphQ](PlanarGraphQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DualPlanarGraph](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/DualPlanarGraph.java)

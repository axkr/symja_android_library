## ConvexHullMesh

```
ConvexHullMesh({p1, p2, ...})
```

> gives the convex hull of the points `{p1, p2, ...}` as a `BoundaryMeshRegion`.

```
ConvexHullMesh({p1, p2, ...}, options)
```

> gives the same region carrying `options`.

The points can have one, two or three numeric coordinates. The result contains the corners of the hull as coordinates, listed in the order in which they appear in the input, and the boundary of the hull as cells: two `Point` cells for points on a line, `Line` cells walking the hull counter-clockwise for points in the plane, and triangular `Polygon` faces for points in space. Points inside the hull, or on its edges, aren't part of the mesh.

The points have to span a hull of their full dimension. Coincident, collinear points in the plane or coplanar points in space print a message and the expression stays unevaluated.

Options are kept where the reference implementation keeps them. `MeshCellStyle` is written out cell by cell as a `Properties` option ahead of `Method`; any other option is kept as given, in a list at the end. An argument that is not an option is reported as points that do not span a hull.

The mesh can be measured and tested like any other region, for example with [Area](Area.md), [Perimeter](Perimeter.md), `Volume`, [RegionCentroid](RegionCentroid.md) or [RegionMember](RegionMember.md). Its parts are returned by [MeshCoordinates](MeshCoordinates.md) and [MeshCells](MeshCells.md).

See:
* [Wikipedia - Convex hull](https://en.wikipedia.org/wiki/Convex_hull)

### Examples

```
>> ConvexHullMesh({{0,0},{2,0},{2,2},{0,2},{1,1}})
BoundaryMeshRegion({{0,0},{2,0},{2,2},{0,2}},{Line({{1,2},{2,3},{3,4},{4,1}})},Method->{SeparateBoundaries->False},WorkingPrecision->Infinity)

>> Area(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2},{1,1}}))
4

>> Perimeter(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2},{1,1}}))
8

>> RegionMember(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}), {3,1})
False
```

The corners keep the order of the input, the boundary still runs counter-clockwise:

```
>> ConvexHullMesh({{2,2},{0,0},{1,1},{0,2},{2,0}})
BoundaryMeshRegion({{2,2},{0,0},{0,2},{2,0}},{Line({{1,3},{3,2},{2,4},{4,1}})},Method->{SeparateBoundaries->False},WorkingPrecision->Infinity)
```

Points on a line and in space:

```
>> ConvexHullMesh({{0},{3},{1}})
BoundaryMeshRegion({{0},{3}},{Point({{1},{2}})},Method->{SeparateBoundaries->False},WorkingPrecision->Infinity)

>> MeshCells(ConvexHullMesh({{0,0,0},{1,0,0},{0,1,0},{0,0,1}}), 2)
{Polygon({1,2,4}),Polygon({1,3,2}),Polygon({1,4,3}),Polygon({2,3,4})}

>> Volume(ConvexHullMesh({{0,0,0},{1,0,0},{0,1,0},{0,0,1}}))
1/6
```

A mesh region is drawn by `Show` and by `ExportString(..., "SVG")`:

```
>> Show(ConvexHullMesh({{0,0,0},{1,0,0},{0,1,0},{0,0,1}}))
```

### Related terms
[BoundaryMeshRegion](BoundaryMeshRegion.md), [ConvexHull](ConvexHull.md), [ConvexHullRegion](ConvexHullRegion.md), [MeshCellCount](MeshCellCount.md), [MeshCells](MeshCells.md), [MeshCoordinates](MeshCoordinates.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ConvexHullMesh](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L1426)

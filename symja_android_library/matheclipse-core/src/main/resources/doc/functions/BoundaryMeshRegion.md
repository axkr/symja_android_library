## BoundaryMeshRegion

```
BoundaryMeshRegion({p1, p2, ...}, {cell1, cell2, ...})
```

> represents the region which is enclosed by the boundary cells `cell1, cell2, ...` over the coordinates `p1, p2, ...`.

```
BoundaryMeshRegion({p1, p2, ...}, cell)
```

> represents the region enclosed by the single boundary cell `cell`.

The coordinates `p1, p2, ...` are points with one, two or three coordinates. A cell refers to the coordinates by their index:

* `Point({i})` - a point; the boundary of a region on a line
* `Line({i, j})` - an edge; the boundary of a region in the plane. `Line({i1, i2, ..., in})` is a walk along the boundary with one edge for each step.
* `Polygon({i, j, k, ...})` - a face; the boundary of a region in space

Several cells of the same kind can be written in the compact form `Line({{i1, j1}, {i2, j2}, ...})`, which is the form [ConvexHullMesh](ConvexHullMesh.md) returns.

`BoundaryMeshRegion` stays as it is written. An empty list of coordinates, a malformed cell or an index which doesn't refer to a coordinate prints a message.

The region can be measured and tested like any other region, for example with [Area](Area.md), [RegionCentroid](RegionCentroid.md) or [RegionMember](RegionMember.md). Its parts are returned by [MeshCoordinates](MeshCoordinates.md) and [MeshCells](MeshCells.md).

See:
* [Wikipedia - Polygon mesh](https://en.wikipedia.org/wiki/Polygon_mesh)

### Examples

The square with the side length `2`, once with one edge for each step of a walk around it, once with its four edges written out:

```
>> BoundaryMeshRegion({{0,0},{2,0},{2,2},{0,2}}, Line({1,2,3,4,1}))
BoundaryMeshRegion({{0,0},{2,0},{2,2},{0,2}},Line({1,2,3,4,1}))

>> Area(BoundaryMeshRegion({{0,0},{2,0},{2,2},{0,2}}, Line({1,2,3,4,1})))
4

>> MeshCells(BoundaryMeshRegion({{0,0},{2,0},{2,2},{0,2}}, Line({1,2,3,4,1})), 1)
{Line({1,2}),Line({2,3}),Line({3,4}),Line({4,1})}

>> Area(BoundaryMeshRegion({{0,0},{2,0},{2,2},{0,2}}, {Line({{1,2},{2,3},{3,4},{4,1}})}))
4

>> RegionCentroid(BoundaryMeshRegion({{0,0},{2,0},{2,2},{0,2}}, Line({1,2,3,4,1})))
{1,1}
```

### Related terms
[BoundaryMeshRegionQ](BoundaryMeshRegionQ.md), [ConvexHullMesh](ConvexHullMesh.md), [MeshCellCount](MeshCellCount.md), [MeshCells](MeshCells.md), [MeshCoordinates](MeshCoordinates.md), [MeshRegionQ](MeshRegionQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of BoundaryMeshRegion](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/MeshFunctions.java#L1433)

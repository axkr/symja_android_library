## ConvexHullMesh

```
ConvexHullMesh({p1, p2, ...})
```

> the convex hull of the points, as a `BoundaryMeshRegion`.

```
ConvexHullMesh({p1, p2, ...}, options)
```

> the same region carrying `options`.

The hull vertices are listed in the order they appear in the input. A two dimensional hull is
bounded by `Line` cells walking it counter-clockwise, a three dimensional one by `Polygon` faces.

Options are kept where the reference implementation keeps them. `MeshCellStyle` is written out cell
by cell as a `Properties` option ahead of `Method` (see [MeshCellStyle](MeshCellStyle.md)); any other
option is kept as given, in a list at the end. An argument that is not an option is reported as
points that do not span a hull.

### Examples

```
>> ConvexHullMesh({{0,0},{2,0},{2,2},{0,2},{1,1}})
BoundaryMeshRegion({{0,0},{2,0},{2,2},{0,2}},{Line({{1,2},{2,3},{3,4},{4,1}})},Method->{SeparateBoundaries->False},WorkingPrecision->Infinity)

>> Area(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}))
4
```

A mesh region is drawn by [Show](Show.md) and by `ExportString(..., "SVG")`:

```
>> Show(ConvexHullMesh({{0,0,0},{1,0,0},{0,1,0},{0,0,1}}))
```

### Related terms
[BoundaryMeshRegion](BoundaryMeshRegion.md), [ConvexHull](ConvexHull.md), [MeshCellStyle](MeshCellStyle.md), [Show](Show.md)

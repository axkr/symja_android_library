## MeshCells

```
MeshCells(mesh, d)
```

> gives the cells of the dimension `d` of the mesh region `mesh`.

```
MeshCells(mesh, {d, i})
```

> gives the `i`-th cell of the dimension `d`.

```
MeshCells(mesh, {d, "Interior"})
MeshCells(mesh, {d, "Boundary"})
```

> gives the cells of the dimension `d` which lie inside the region, or on its boundary.

```
MeshCells(mesh, All)
```

> gives the cells of all dimensions below the dimension of the space the mesh lies in.

A cell refers to the coordinates of the mesh, see [MeshCoordinates](MeshCoordinates.md), by their index: the cells of dimension `0` are `Point({i})`, of dimension `1` `Line({i, j})` and of dimension `2` `Polygon({i, j, k, ...})`. Every coordinate is a cell of dimension `0`. The edges of a mesh in space are derived from its faces, and the cell of full dimension of a [BoundaryMeshRegion](BoundaryMeshRegion.md) in the plane is the polygon which its boundary encloses.

`{d, All}` is the same as `d`. An index which doesn't refer to a cell, a dimension without cells, or an expression which isn't a valid mesh region leaves the expression unevaluated.

### Examples

```
>> MeshCells(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}), 0)
{Point({1}),Point({2}),Point({3}),Point({4})}

>> MeshCells(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}), 1)
{Line({1,2}),Line({2,3}),Line({3,4}),Line({4,1})}

>> MeshCells(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}), 2)
{Polygon({1,2,3,4})}

>> MeshCells(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}), {1,2})
Line({2,3})
```

The edges of a tetrahedron are derived from its faces:

```
>> MeshCells(ConvexHullMesh({{0,0,0},{1,0,0},{0,1,0},{0,0,1}}), 1)
{Line({1,2}),Line({2,4}),Line({4,1}),Line({1,3}),Line({3,2}),Line({4,3})}
```

The Delaunay triangulation of a rectangle with the point `{2,1}` inside it:

```
>> MeshCells(DelaunayMesh({{0,0},{4,0},{4,3},{0,3},{2,1}}), 2)
{Polygon({1,2,5}),Polygon({1,5,4}),Polygon({2,3,5}),Polygon({3,4,5})}

>> MeshCells(DelaunayMesh({{0,0},{4,0},{4,3},{0,3},{2,1}}), {0,"Interior"})
{Point({5})}

>> MeshCells(DelaunayMesh({{0,0},{4,0},{4,3},{0,3},{2,1}}), {1,"Boundary"})
{Line({1,2}),Line({4,1}),Line({2,3}),Line({3,4})}
```

### Related terms
[BoundaryMeshRegion](BoundaryMeshRegion.md), [ConvexHullMesh](ConvexHullMesh.md), [MeshCellCount](MeshCellCount.md), [MeshCoordinates](MeshCoordinates.md), [MeshRegionQ](MeshRegionQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of MeshCells](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/MeshFunctions.java#L1518)

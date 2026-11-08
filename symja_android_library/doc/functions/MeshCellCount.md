## MeshCellCount

```
MeshCellCount(mesh)
```

> gives the list of the numbers of the cells of each dimension of the mesh region `mesh`, starting with dimension `0`.

```
MeshCellCount(mesh, d)
```

> gives the number of the cells of the dimension `d`.

The cells are counted as [MeshCells](MeshCells.md) returns them. The list of all counts includes the cells of full dimension only if the mesh stores them, as a `MeshRegion` from `DelaunayMesh` does; a [BoundaryMeshRegion](BoundaryMeshRegion.md) only stores the cells of its boundary. For an expression which isn't a valid mesh region the expression stays unevaluated.

### Examples

The square has four corners and four edges:

```
>> MeshCellCount(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}))
{4,4}

>> MeshCellCount(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}), 1)
4

>> MeshCellCount(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}), 2)
1
```

The tetrahedron has four corners, six edges and four faces:

```
>> MeshCellCount(ConvexHullMesh({{0,0,0},{1,0,0},{0,1,0},{0,0,1}}))
{4,6,4}
```

The Delaunay triangulation stores its four triangles:

```
>> MeshCellCount(DelaunayMesh({{0,0},{4,0},{4,3},{0,3},{2,1}}))
{5,8,4}
```

### Related terms
[BoundaryMeshRegion](BoundaryMeshRegion.md), [ConvexHullMesh](ConvexHullMesh.md), [MeshCells](MeshCells.md), [MeshCoordinates](MeshCoordinates.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of MeshCellCount](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/MeshFunctions.java#L1579)

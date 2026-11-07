## MeshCoordinates

```
MeshCoordinates(mesh)
```

> gives the list of the coordinates of the mesh region `mesh`.

The cells of a mesh region refer to these coordinates by their index, see [MeshCells](MeshCells.md). For an expression which isn't a valid `MeshRegion` or [BoundaryMeshRegion](BoundaryMeshRegion.md) the expression stays unevaluated.

### Examples

The point `{1,1}` lies inside the hull, so it isn't a coordinate of the mesh:

```
>> MeshCoordinates(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2},{1,1}}))
{{0,0},{2,0},{2,2},{0,2}}
```

```
>> MeshCoordinates(DelaunayMesh({{0,0},{4,0},{4,3},{0,3},{2,1}}))
{{0,0},{4,0},{4,3},{0,3},{2,1}}
```

A region primitive isn't a mesh region:

```
>> MeshCoordinates(Disk())
MeshCoordinates(Disk({0,0}))
```

### Related terms
[BoundaryMeshRegion](BoundaryMeshRegion.md), [ConvexHullMesh](ConvexHullMesh.md), [MeshCellCount](MeshCellCount.md), [MeshCells](MeshCells.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of MeshCoordinates](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/MeshFunctions.java#L1496)

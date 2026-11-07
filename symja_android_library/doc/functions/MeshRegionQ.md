## MeshRegionQ

```
MeshRegionQ(reg)
```

> returns `True` if `reg` is a valid mesh region, and `False` otherwise.

Mesh regions are a `MeshRegion`, which consists of the cells of the region itself, as returned by `DelaunayMesh`, and a [BoundaryMeshRegion](BoundaryMeshRegion.md), which consists of the cells of its boundary, as returned by [ConvexHullMesh](ConvexHullMesh.md). A mesh region is valid if it has a non-empty list of coordinates of the same dimension one, two or three, and every cell is a `Point`, `Line` or `Polygon` whose indices refer to the coordinates. A region primitive like a `Polygon` isn't a mesh region.

### Examples

```
>> MeshRegionQ(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}))
True

>> MeshRegionQ(DelaunayMesh({{0,0},{3,0},{0,2}}))
True

>> MeshRegionQ(Polygon({{0,0},{2,0},{2,2}}))
False
```

### Related terms
[BoundaryMeshRegion](BoundaryMeshRegion.md), [BoundaryMeshRegionQ](BoundaryMeshRegionQ.md), [ConvexHullMesh](ConvexHullMesh.md), [MeshCells](MeshCells.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of MeshRegionQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/MeshFunctions.java#L1478)

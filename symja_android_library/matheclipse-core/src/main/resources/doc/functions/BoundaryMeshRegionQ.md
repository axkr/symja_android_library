## BoundaryMeshRegionQ

```
BoundaryMeshRegionQ(reg)
```

> returns `True` if `reg` is a valid `BoundaryMeshRegion`, and `False` otherwise.

A `BoundaryMeshRegion` is valid if it has a non-empty list of coordinates of the same dimension one, two or three, and every cell is a `Point`, `Line` or `Polygon` whose indices refer to the coordinates. A `MeshRegion`, as returned by `DelaunayMesh`, and a region primitive like a `Polygon` give `False`; use [MeshRegionQ](MeshRegionQ.md) to test for both kinds of mesh regions.

### Examples

```
>> BoundaryMeshRegionQ(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}))
True

>> BoundaryMeshRegionQ(BoundaryMeshRegion({{0,0},{2,0},{2,2},{0,2}}, Line({1,2,3,4,1})))
True

>> BoundaryMeshRegionQ(DelaunayMesh({{0,0},{3,0},{0,2}}))
False

>> BoundaryMeshRegionQ(Polygon({{0,0},{2,0},{2,2}}))
False
```

### Related terms
[BoundaryMeshRegion](BoundaryMeshRegion.md), [ConvexHullMesh](ConvexHullMesh.md), [MeshRegionQ](MeshRegionQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of BoundaryMeshRegionQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/MeshFunctions.java#L1460)

## RegionBoundary

```
RegionBoundary(reg)
```

> gives the boundary of the region `reg`.

The boundary of a `Polygon` or a `Triangle` is the closed `Line` through its vertices, the boundary of a `Disk` is a `Circle` and the boundary of a `Ball` is a `Sphere`. The boundary of a mesh region is the `MeshRegion` of its boundary cells. For other regions the expression stays unevaluated.

See:
* [Wikipedia - Boundary (topology)](https://en.wikipedia.org/wiki/Boundary_(topology))

### Examples

```
>> RegionBoundary(Polygon({{0,0},{1,0},{0,1}}))
Line({{0,0},{1,0},{0,1},{0,0}})

>> RegionBoundary(Disk({0,0},2))
Circle({0,0},2)

>> RegionBoundary(Ball({0,0,0},2))
Sphere({0,0,0},2)

>> RegionBoundary(ConvexHullRegion({{0,0},{2,0},{2,2},{0,2}}))
Line({{0,0},{2,0},{2,2},{0,2},{0,0}})
```

The boundary of a mesh region:

```
>> MeshCells(RegionBoundary(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}})), 1)
{Line({1,2}),Line({2,3}),Line({3,4}),Line({4,1})}
```

### Related terms
[ConvexHullRegion](ConvexHullRegion.md), [MeshCells](MeshCells.md), [Polygon](Polygon.md), [Sphere](Sphere.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of RegionBoundary](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L1378)

## ConvexRegionQ

```
ConvexRegionQ(reg)
```

> returns `True` if the region `reg` is convex, and `False` if it is not.

A region is convex if it contains the whole segment between any two of its points. `ConvexRegionQ` knows the convex region primitives, such as `Triangle`, `Rectangle`, `Cuboid`, `Disk`, `Ball`, `Ellipsoid`, `Simplex`, `Tetrahedron`, `Parallelepiped`, `HalfPlane`, `HalfSpace`, `Cone` and `Cylinder`, and that an `Annulus` is not convex. A `Polygon` with numeric vertices and a mesh region are tested for convexity.

For other regions, and for expressions which aren't regions, the expression stays unevaluated.

See:
* [Wikipedia - Convex set](https://en.wikipedia.org/wiki/Convex_set)

### Examples

```
>> ConvexRegionQ(Disk())
True

>> ConvexRegionQ(Annulus())
False

>> ConvexRegionQ(Polygon({{0,0},{2,0},{2,2},{0,2}}))
True
```

The vertex `{1,1/10}` dents the square:

```
>> ConvexRegionQ(Polygon({{0,0},{2,0},{1,1/10},{2,2},{0,2}}))
False
```

Mesh regions and the regions of [ConvexHullRegion](ConvexHullRegion.md):

```
>> ConvexRegionQ(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}))
True

>> ConvexRegionQ(ConvexHullRegion({{0,0},{2,0},{2,2},{0,2}}))
True
```

### Related terms
[ConstantRegionQ](ConstantRegionQ.md), [ConvexHullRegion](ConvexHullRegion.md), [MeshRegionQ](MeshRegionQ.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ConvexRegionQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L1318)

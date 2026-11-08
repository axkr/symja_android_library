## ConvexHullRegion

```
ConvexHullRegion({p1, p2, ...})
```

> gives the convex hull of the points `{p1, p2, ...}` as a region.

```
ConvexHullRegion(reg)
```

> gives the convex hull of the region `reg`.

The points can have one, two or three numeric coordinates. The hull is the simplest region which describes it:

* a `Point` if all points coincide
* a `Line` between the two extreme points if the points are collinear
* a `Polygon` of the corners in counter-clockwise order if the points lie in a plane; the second argument of the polygon lists the indices of its corners
* a `Polyhedron` of the corners and the triangular faces if the points span a solid

A full `Disk`, a `Ball`, an `Ellipsoid`, a `HalfPlane` and a `HalfSpace` are their own hull. For a region with corners, such as a `Triangle`, a `Rectangle`, a `Polygon` or a mesh region, the hull of its corners is returned. Points with symbolic coordinates leave the expression unevaluated.

See:
* [Wikipedia - Convex hull](https://en.wikipedia.org/wiki/Convex_hull)

### Examples

```
>> ConvexHullRegion({{0,0},{2,0},{2,2},{0,2},{1,1}})
Polygon({{0,0},{2,0},{2,2},{0,2}},{1,2,3,4})

>> Area(ConvexHullRegion({{0,0},{2,0},{2,2},{0,2},{1,1}}))
4
```

Coincident and collinear points:

```
>> ConvexHullRegion({{1,1},{1,1}})
Point({1,1})

>> ConvexHullRegion({{0,0},{1,1},{2,2}})
Line({{0,0},{2,2}})
```

Points in space:

```
>> ConvexHullRegion({{0,0,0},{1,0,0},{0,1,0},{0,0,1}})
Polyhedron({{0,0,0},{1,0,0},{0,1,0},{0,0,1}},{{1,2,4},{1,3,2},{1,4,3},{2,3,4}})

>> ConvexHullRegion({{0,0,0},{1,0,0},{0,1,0},{1,1,0}})
Polygon({{0,0,0},{1,0,0},{1,1,0},{0,1,0}},{1,2,3,4})
```

The hull of regions:

```
>> ConvexHullRegion(Disk())
Disk({0,0})

>> ConvexHullRegion(Rectangle({0,0},{2,1}))
Polygon({{0,0},{2,0},{2,1},{0,1}},{1,2,3,4})

>> ConvexHullRegion(Polygon({{0,0},{2,0},{1,1/2},{2,2},{0,2}}))
Polygon({{0,0},{2,0},{2,2},{0,2}},{1,2,3,4})
```

### Related terms
[ConvexHull](ConvexHull.md), [ConvexHullMesh](ConvexHullMesh.md), [ConvexRegionQ](ConvexRegionQ.md), [Polygon](Polygon.md), [RegionBoundary](RegionBoundary.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ConvexHullRegion](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L1166)

## ConvexHull

```
ConvexHull({p1, p2, ...})
```

> gives the indices of the points `{p1, p2, ...}` in the plane which lie on their convex hull, in counter-clockwise order.

The points have to be pairs `{x, y}`; points in other dimensions, or a list which isn't a list of points, leave the expression unevaluated. Pairs which don't span a two dimensional hull - collinear or coincident points, fewer than three points, or points which aren't numeric - print a message and the expression stays unevaluated.

`ConvexHull` returns indices into the list of points. Use [ConvexHullRegion](ConvexHullRegion.md) or [ConvexHullMesh](ConvexHullMesh.md) to get the hull as a region.

See:
* [Wikipedia - Convex hull](https://en.wikipedia.org/wiki/Convex_hull)

### Options

- `AllPoints` - if `True` (default value) the points on the edges of the hull are included; if `False` only the corners of the hull are returned

### Examples

The point `{1,1}` lies inside the square:

```
>> ConvexHull({{0,0},{2,0},{2,2},{0,2},{1,1}})
{1,2,3,4}
```

The hull starts at the point with the smallest `y` coordinate, or the smallest `x` coordinate among several such points:

```
>> ConvexHull({{3,1},{0,0},{1,4},{2,2}})
{2,1,3}
```

The point `{1,2}` lies on an edge of the hull:

```
>> ConvexHull({{0,0},{2,0},{2,2},{1,2},{0,2}})
{1,2,3,4,5}

>> ConvexHull({{0,0},{2,0},{2,2},{1,2},{0,2}}, AllPoints->False)
{1,2,3,5}
```

### Related terms
[ConvexHullMesh](ConvexHullMesh.md), [ConvexHullRegion](ConvexHullRegion.md), [CoordinateBoundingBox](CoordinateBoundingBox.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ConvexHull](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L1274)

## CircularArcThrough

```
CircularArcThrough({p1, p2, ...})
```

> gives the circular arc through the points `p1, p2, ...` in the plane.

```
CircularArcThrough({p1, p2, ...}, center)
```

> gives the circular arc through the points around the given `center`.

```
CircularArcThrough({p1, p2, ...}, center, r)
```

> gives the circular arc through the points around the given `center` with the radius `r`.

The arc is returned as `Circle(center, r, {theta1, theta2})`. Without a `center`, or with the `center` `Automatic`, the circle is computed from the points: two points are the ends of a diameter, three or more points have to lie on a common circle. A given `center` must have the same distance from all points.

The angles of the points are measured counter-clockwise from the center in the range `0 <= theta < 2*Pi`. The arc starts at the smallest and ends at the largest of these angles, independent of the order in which the points are given.

If the points don't lie on a common circle, aren't numeric, aren't in the plane, or don't fit the given `center` or radius `r`, the expression stays unevaluated.

See:
* [Wikipedia - Circular arc](https://en.wikipedia.org/wiki/Circular_arc)

### Examples

The upper half of the unit circle:

```
>> CircularArcThrough({{1, 0}, {0, 1}, {-1, 0}})
Circle({0,0},1,{0,Pi})

>> CircularArcThrough({{-1, 0}, {0, 1}, {1, 0}})
Circle({0,0},1,{0,Pi})
```

The circle through three points:

```
>> CircularArcThrough({{1, 0}, {0, 1}, {2, 3}})
Circle({3/2,3/2},Sqrt(5/2),{ArcTan(3),Pi+ArcTan(3)})

>> CircularArcThrough({{Sqrt(2), 0}, {1, 1}, {0, Sqrt(2)}})
Circle({0,0},Sqrt(2),{0,Pi/2})

>> CircularArcThrough({{1, 0}, {0, 1}, {-1, 0}, {0, -1}})
Circle({0,0},1,{0,3/2*Pi})
```

Without a center, two points are the ends of a diameter:

```
>> CircularArcThrough({{0, 0}, {2, 0}})
Circle({1,0},1,{0,Pi})
```

A given center, without and with a radius:

```
>> CircularArcThrough({{3, 0}, {0, 3}}, {0, 0})
Circle({0,0},3,{0,Pi/2})

>> CircularArcThrough({{0, 0}, {2, 0}}, {1, 0}, 1)
Circle({1,0},1,{0,Pi})

>> ArcLength(CircularArcThrough({{3, 0}, {0, 3}}, {0, 0}))
3/2*Pi
```

The points don't have the same distance from the center, or the radius doesn't fit:

```
>> CircularArcThrough({{1, 0}, {0, 2}}, {0, 0})
CircularArcThrough({{1,0},{0,2}},{0,0})

>> CircularArcThrough({{1, 0}, {0, 1}}, {0, 0}, 2)
CircularArcThrough({{1,0},{0,1}},{0,0},2)
```

Collinear points don't lie on a circle:

```
>> CircularArcThrough({{0, 0}, {1, 0}, {2, 0}})
CircularArcThrough({{0,0},{1,0},{2,0}})
```

### Related terms
[ArcLength](ArcLength.md), [TriangleConstruct](TriangleConstruct.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of CircularArcThrough](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L2403)

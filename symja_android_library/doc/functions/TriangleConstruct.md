## TriangleConstruct

```
TriangleConstruct(tri, "type")
```

> gives the geometric construct of the given `"type"` for the triangle `tri`.

```
TriangleConstruct(tri, {"type", p})
```

> gives the vertex dependent construct of the given `"type"` for the vertex `p` of the triangle `tri`.

```
TriangleConstruct(tri)
```

> gives the triangle `tri` itself.

The triangle `tri` can be given as `Triangle({p1, p2, p3})`, `Polygon({p1, p2, p3})` or as the list of vertices `{p1, p2, p3}`. The vertices can have two or more coordinates. `Triangle()` is the unit triangle `Triangle({{0,0},{1,0},{0,1}})`.

Depending on the `"type"`, the result is a `Point`, a `Line`, an `InfiniteLine`, a `Circle` or a `Triangle`:

* every center of [TriangleCenter](TriangleCenter.md), such as `"Centroid"`, `"Incenter"` or `{"Excenter", p}`, gives the center as a `Point`
* `"Altitude"`, `"Median"`, `"AngleBisectingCevian"` and `"Symmedian"` give the segment from the vertex `p` to the opposite side as a `Line`
* `{"Cevian", center, p}` gives the segment from `p` through `center` to the opposite side as a `Line`
* `"OppositeSide"` gives the side opposite of `p` as a `Line`
* `"Boundary"` gives the closed boundary of the triangle as a `Line`
* `"AngleBisector"` and `"ExteriorAngleBisector"` give the interior and the exterior bisector of the angle at `p` as an `InfiniteLine`
* `"PerpendicularBisector"` gives the perpendicular bisector of the side opposite of `p` as an `InfiniteLine`
* `"EulerLine"` gives the line through the centroid and the orthocenter as an `InfiniteLine`
* `"Incircle"`, `"Circumcircle"` and `"NinePointCircle"` give a `Circle`, and `{"Excircle", p}` gives the excircle opposite of `p`
* `"Triangle"` gives the triangle itself, `"MedialTriangle"` the triangle of the midpoints of the sides, and `"AntimedialTriangle"` the triangle whose medial triangle is `tri`

An `InfiniteLine` is written either through two points, `InfiniteLine({q1, q2})`, or through a point and a direction, `InfiniteLine(q, v)`.

The vertex `p` is the index `1`, `2` or `3`, the coordinates of one of the vertices, `Point(coordinates)` or `All` for the list of the constructs of all three vertices. A vertex dependent `"type"` given without a vertex uses the second vertex.

The circles are only constructed for a triangle in the plane, and the Euler line only if the centroid and the orthocenter differ, which is not the case for an equilateral triangle. Otherwise the expression stays unevaluated. An unknown construct or vertex prints a message and the expression stays unevaluated.

See:
* [Wikipedia - Triangle](https://en.wikipedia.org/wiki/Triangle)
* [Wikipedia - Euler line](https://en.wikipedia.org/wiki/Euler_line)
* [Wikipedia - Nine-point circle](https://en.wikipedia.org/wiki/Nine-point_circle)

### Examples

Centers as points:

```
>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Centroid")
Point({4/3,1})

>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Excenter", 1})
Point({6,6})
```

Segments from a vertex to the opposite side:

```
>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Altitude", 1})
Line({{0,0},{36/25,48/25}})

>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Median", All})
{Line({{0,0},{2,3/2}}),Line({{4,0},{0,3/2}}),Line({{0,3},{2,0}})}

>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Cevian", "Incenter", 1})
Line({{0,0},{12/7,12/7}})

>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"OppositeSide", All})
{Line({{4,0},{0,3}}),Line({{0,0},{0,3}}),Line({{0,0},{4,0}})}

>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Boundary")
Line({{0,0},{4,0},{0,3},{0,0}})
```

Without a vertex the second vertex is used:

```
>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Altitude")
Line({{4,0},{0,0}})
```

Infinite lines:

```
>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"AngleBisector", 1})
InfiniteLine({0,0},{1,1})

>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"PerpendicularBisector", 1})
InfiniteLine({2,3/2},{3,4})

>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), "EulerLine")
InfiniteLine({{4/3,1},{0,0}})
```

Circles:

```
>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Incircle")
Circle({1,1},1)

>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Circumcircle")
Circle({2,3/2},5/2)

>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), "NinePointCircle")
Circle({1,3/4},5/4)

>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Excircle", All})
{Circle({6,6},6),Circle({-2,2},2),Circle({3,-3},3)}
```

Derived triangles:

```
>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), "MedialTriangle")
Triangle({{2,3/2},{0,3/2},{2,0}})

>> TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), "AntimedialTriangle")
Triangle({{4,3},{-4,3},{4,-3}})
```

The constructs can be measured and tested like any other region:

```
>> Area(TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), "MedialTriangle"))
3/2

>> ArcLength(TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Altitude", 1}))
12/5

>> RegionMember(TriangleConstruct(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Circumcircle"), {0, 3})
True
```

A triangle in 3D space has no circles, and an equilateral triangle no Euler line:

```
>> TriangleConstruct(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), "Circumcircle")
TriangleConstruct(Triangle({{0,0,0},{4,0,0},{0,3,0}}),Circumcircle)

>> TriangleConstruct(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), "EulerLine")
TriangleConstruct(Triangle({{0,0},{1,0},{1/2,Sqrt(3)/2}}),EulerLine)
```

### Related terms
[ArcLength](ArcLength.md), [Area](Area.md), [Polygon](Polygon.md), [TriangleCenter](TriangleCenter.md), [TriangleMeasurement](TriangleMeasurement.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of TriangleConstruct](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/TriangleConstruct.java)

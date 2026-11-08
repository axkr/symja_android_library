## TriangleMeasurement

```
TriangleMeasurement(tri)
```

> gives the area of the triangle `tri`.

```
TriangleMeasurement(tri, "type")
```

> gives the measurement of the given `"type"` of the triangle `tri`.

```
TriangleMeasurement(tri, {"type", p})
```

> gives the vertex dependent measurement of the given `"type"` for the vertex `p` of the triangle `tri`.

The triangle `tri` can be given as `Triangle({p1, p2, p3})`, `Polygon({p1, p2, p3})` or as the list of vertices `{p1, p2, p3}`. The vertices can have two or more coordinates; a triangle in 3D space is measured in its own plane. `Triangle()` is the unit triangle `Triangle({{0,0},{1,0},{0,1}})`.

These measurements don't depend on a vertex:

* `"Area"` - the area, the default
* `"Perimeter"` - the sum of the side lengths
* `"Semiperimeter"` - half of the perimeter
* `"Inradius"` - the radius of the inscribed circle
* `"Circumradius"` - the radius of the circle through the three vertices
* `"NinePointRadius"` - the radius of the nine-point circle, half of the circumradius

These measurements depend on a vertex `p`:

* `"Exradius"` - the radius of the excircle opposite of `p`
* `"Height"` - the length of the altitude from `p`
* `"InteriorAngle"` - the angle at `p` inside the triangle
* `"ExteriorAngle"` - `Pi` minus the interior angle at `p`
* `"FullExteriorAngle"` - `2*Pi` minus the interior angle at `p`

The vertex `p` is the index `1`, `2` or `3`, the coordinates of one of the vertices, `Point(coordinates)` or `All` for the list of the measurements of all three vertices. A vertex dependent `"type"` given without a vertex uses the second vertex.

An unknown measurement or vertex prints a message and the expression stays unevaluated. So does a triangle whose vertices are collinear.

See:
* [Wikipedia - Triangle](https://en.wikipedia.org/wiki/Triangle)
* [Wikipedia - Incircle and excircles](https://en.wikipedia.org/wiki/Incircle_and_excircles)

### Examples

The measurements of the 3-4-5 right triangle:

```
>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}))
6

>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Perimeter")
12

>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Semiperimeter")
6

>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Inradius")
1

>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Circumradius")
5/2

>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), "NinePointRadius")
5/4
```

Vertex dependent measurements. Without a vertex the second vertex is used:

```
>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Exradius")
2

>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Exradius", All})
{6,2,3}

>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Height", All})
{12/5,4,3}
```

The angles are exact, `N` gives their numerical values:

```
>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"InteriorAngle", All})
{Pi/2,ArcCos(4/5),ArcCos(3/5)}

>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"ExteriorAngle", All})
{Pi/2,Pi-ArcCos(4/5),Pi-ArcCos(3/5)}

>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"FullExteriorAngle", 1})
3/2*Pi

>> TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"InteriorAngle", Point({4, 0})})
ArcCos(4/5)

>> N(TriangleMeasurement(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"InteriorAngle", All}))
{1.5708,0.643501,0.927295}
```

Symbolic vertices:

```
>> TriangleMeasurement(Triangle({{0, 0}, {x, 0}, {0, y}}), "Area")
Abs(x*y)/2
```

Other forms of a triangle:

```
>> TriangleMeasurement(Triangle(), "Perimeter")
2+Sqrt(2)

>> TriangleMeasurement({{0, 0}, {4, 0}, {0, 3}}, "Inradius")
1

>> TriangleMeasurement(SSSTriangle(3, 4, 5), "Circumradius")
5/2

>> TriangleMeasurement(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), "Area")
6

>> TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {1/2, Sqrt(3)/2}}), "Inradius")
1/(2*Sqrt(3))
```

Collinear vertices don't define a triangle:

```
>> TriangleMeasurement(Triangle({{0, 0}, {1, 0}, {2, 0}}), "Area")
TriangleMeasurement(Triangle({{0,0},{1,0},{2,0}}),Area)
```

### Related terms
[Area](Area.md), [Perimeter](Perimeter.md), [PolygonAngle](PolygonAngle.md), [SSSTriangle](SSSTriangle.md), [TriangleCenter](TriangleCenter.md), [TriangleConstruct](TriangleConstruct.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of TriangleMeasurement](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/TriangleMeasurement.java)

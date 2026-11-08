## TriangleCenter

```
TriangleCenter(tri)
```

> gives the centroid of the triangle `tri`.

```
TriangleCenter(tri, "type")
```

> gives the center of the given `"type"` of the triangle `tri`.

```
TriangleCenter(tri, {"type", p})
```

> gives the vertex dependent center of the given `"type"` for the vertex `p` of the triangle `tri`.

The triangle `tri` can be given as `Triangle({p1, p2, p3})`, `Polygon({p1, p2, p3})` or as the list of vertices `{p1, p2, p3}`. The vertices can have two or more coordinates. `Triangle()` is the unit triangle `Triangle({{0,0},{1,0},{0,1}})`.

These centers don't depend on a vertex:

* `"Centroid"` - the intersection of the medians, the default
* `"Circumcenter"` - the center of the circle through the three vertices
* `"Incenter"` - the center of the inscribed circle
* `"Orthocenter"` - the intersection of the altitudes
* `"NinePointCenter"` - the center of the nine-point circle, halfway between the circumcenter and the orthocenter
* `"SymmedianPoint"` - the intersection of the symmedians

These centers depend on a vertex `p`:

* `"Excenter"` - the center of the excircle opposite of `p`
* `"Midpoint"` - the midpoint of the side opposite of `p`
* `"Foot"` - the foot of the altitude from `p`
* `"AngleBisectingCevianEndpoint"` - the point where the bisector of the angle at `p` meets the opposite side
* `"SymmedianEndpoint"` - the point where the symmedian from `p` meets the opposite side
* `"CevianEndpoint"` - used as `{"CevianEndpoint", center, p}`: the point where the line from `p` through `center` meets the opposite side. `center` is the name of a center, or a list `{"type", q}` for a vertex dependent center.

The vertex `p` is the index `1`, `2` or `3`, the coordinates of one of the vertices, `Point(coordinates)` or `All` for the list of the centers of all three vertices. A vertex dependent `"type"` given without a vertex uses the second vertex.

An unknown center or vertex prints a message and the expression stays unevaluated. So does a triangle whose vertices are collinear.

See:
* [Wikipedia - Triangle center](https://en.wikipedia.org/wiki/Triangle_center)
* [Wikipedia - Encyclopedia of Triangle Centers](https://en.wikipedia.org/wiki/Encyclopedia_of_Triangle_Centers)

### Examples

The centers of the 3-4-5 right triangle:

```
>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}))
{4/3,1}

>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Incenter")
{1,1}

>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Circumcenter")
{2,3/2}

>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Orthocenter")
{0,0}

>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), "NinePointCenter")
{1,3/4}

>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), "SymmedianPoint")
{18/25,24/25}
```

Symbolic vertices:

```
>> TriangleCenter(Triangle({{a, b}, {c, d}, {e, f}}))
{1/3*(a+c+e),1/3*(b+d+f)}
```

Vertex dependent centers. Without a vertex the second vertex is used:

```
>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), "Excenter")
{-2,2}

>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Excenter", All})
{{6,6},{-2,2},{3,-3}}

>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Foot", All})
{{36/25,48/25},{0,0},{0,0}}
```

A vertex can be given by its index or by its coordinates:

```
>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Midpoint", 1})
{2,3/2}

>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"Midpoint", {4, 0}})
{0,3/2}
```

The endpoint of the cevian from the first vertex through the incenter, and of the cevian from the second vertex through the first excenter:

```
>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"CevianEndpoint", "Incenter", 1})
{12/7,12/7}

>> TriangleCenter(Triangle({{0, 0}, {4, 0}, {0, 3}}), {"CevianEndpoint", {"Excenter", 1}, 2})
{0,-12}
```

Other forms of a triangle:

```
>> TriangleCenter(Triangle(), "Circumcenter")
{1/2,1/2}

>> TriangleCenter({{0, 0}, {4, 0}, {0, 3}}, "Incenter")
{1,1}

>> TriangleCenter(Triangle({{0, 0, 0}, {4, 0, 0}, {0, 3, 0}}), "Circumcenter")
{2,3/2,0}
```

Collinear vertices don't define a triangle:

```
>> TriangleCenter(Triangle({{0, 0}, {1, 0}, {2, 0}}))
TriangleCenter(Triangle({{0,0},{1,0},{2,0}}))
```

### Related terms
[Polygon](Polygon.md), [SSSTriangle](SSSTriangle.md), [TriangleConstruct](TriangleConstruct.md), [TriangleMeasurement](TriangleMeasurement.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of TriangleCenter](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/TriangleCenter.java)

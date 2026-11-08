## PolygonAngle

```
PolygonAngle(poly)
```

> gives the list of the interior angles at the vertices of the polygon `poly`.

```
PolygonAngle(poly, p)
```

> gives the interior angle at the vertex `p` of the polygon `poly`.

```
PolygonAngle(poly, "type")
PolygonAngle(poly, p, "type")
```

> gives the angles of the given `"type"`.

The polygon `poly` can be given as `Polygon({p1, p2, ...})`, `Triangle({p1, p2, p3})` or as the list of vertices `{p1, p2, ...}`, in the order in which they are connected.

The `"type"` is one of:

* `"Interior"` - the angle at the vertex inside the polygon, the default
* `"Exterior"` - `Pi` minus the interior angle; it is negative at a reflex vertex
* `"FullExterior"` - `2*Pi` minus the interior angle

The list of all angles starts at the vertex which comes first in `Sort` and runs counter-clockwise around the polygon, whatever the orientation of the given vertices and the vertex they start with. At a reflex vertex of a concave polygon the interior angle is greater than `Pi`.

The vertex `p` is the index of a vertex in this order, the coordinates of one of the vertices, `Point(coordinates)` or `All`.

An unknown `"type"` or vertex prints a message and the expression stays unevaluated. A polygon which isn't in the plane, has symbolic coordinates or collinear vertices stays unevaluated, too.

See:
* [Wikipedia - Internal and external angles](https://en.wikipedia.org/wiki/Internal_and_external_angles)

### Examples

```
>> PolygonAngle(Polygon({{0, 0}, {4, 0}, {0, 3}}))
{Pi/2,ArcCos(4/5),ArcCos(3/5)}

>> PolygonAngle(Polygon({{0, 0}, {4, 0}, {0, 3}}), {4, 0})
ArcCos(4/5)

>> N(PolygonAngle(Polygon({{0, 0}, {4, 0}, {0, 3}})))
{1.5708,0.643501,0.927295}
```

The angles of a clockwise polygon are listed counter-clockwise, too, starting at `{0, 0}`. The index of a vertex counts in that order:

```
>> PolygonAngle(Polygon({{0, 3}, {4, 0}, {0, 0}}))
{Pi/2,ArcCos(4/5),ArcCos(3/5)}

>> PolygonAngle(Polygon({{0, 3}, {4, 0}, {0, 0}}), 1)
Pi/2
```

A concave polygon has a reflex vertex at `{1, 1}`:

```
>> PolygonAngle(Polygon({{0, 0}, {2, 0}, {2, 2}, {1, 1}, {0, 2}}))
{Pi/2,Pi/2,Pi/4,3/2*Pi,Pi/4}

>> PolygonAngle(Polygon({{0, 0}, {2, 0}, {2, 2}, {1, 1}, {0, 2}}), {1, 1})
3/2*Pi

>> PolygonAngle(Polygon({{0, 0}, {2, 0}, {2, 2}, {1, 1}, {0, 2}}), "Exterior")
{Pi/2,Pi/2,3/4*Pi,-Pi/2,3/4*Pi}

>> PolygonAngle(Polygon({{0, 0}, {2, 0}, {2, 2}, {1, 1}, {0, 2}}), {1, 1}, "FullExterior")
Pi/2
```

The interior angles of a polygon with `n` vertices add up to `(n-2)*Pi`, and the exterior angles to `2*Pi`:

```
>> Total(PolygonAngle(Polygon({{0, 0}, {2, 0}, {2, 2}, {1, 1}, {0, 2}})))
3*Pi

>> Total(PolygonAngle(Polygon({{0, 0}, {2, 0}, {2, 2}, {1, 1}, {0, 2}}), "Exterior"))
2*Pi
```

### Related terms
[Polygon](Polygon.md), [PolygonCoordinates](PolygonCoordinates.md), [TriangleMeasurement](TriangleMeasurement.md), [VectorAngle](VectorAngle.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of PolygonAngle](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/PolygonAngle.java)

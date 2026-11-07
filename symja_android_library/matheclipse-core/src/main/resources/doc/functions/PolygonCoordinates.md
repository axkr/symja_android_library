## PolygonCoordinates

```
PolygonCoordinates(poly)
```

> gives the coordinates of the vertices of the polygon `poly`.

The polygon `poly` can be given as `Polygon({p1, p2, ...})`, `Triangle({p1, p2, p3})` or as the list of vertices `{p1, p2, ...}`.

The result doesn't keep the order in which the vertices are connected: the coordinates are sorted, so that two descriptions of the same polygon give the same result. A vertex which repeats the previous one is dropped. A polygon whose vertices are collinear encloses no area, and the expression stays unevaluated.

### Examples

```
>> PolygonCoordinates(Polygon({{1, 0}, {0, 1}, {-1, 0}, {0, -1}}))
{{-1,0},{0,-1},{0,1},{1,0}}

>> PolygonCoordinates(Triangle({{0, 0}, {4, 0}, {0, 3}}))
{{0,0},{0,3},{4,0}}
```

A repeated vertex is dropped:

```
>> PolygonCoordinates({{0, 0}, {1, 0}, {1, 0}, {1, 1}, {0, 1}})
{{0,0},{0,1},{1,0},{1,1}}
```

The vertices can have more than two coordinates:

```
>> PolygonCoordinates(Polygon({{0, 0, 0}, {1, 0, 0}, {0, 1, 1}}))
{{0,0,0},{0,1,1},{1,0,0}}
```

Collinear vertices don't define a polygon:

```
>> PolygonCoordinates(Polygon({{0, 0}, {1, 0}, {2, 0}}))
PolygonCoordinates(Polygon({{0,0},{1,0},{2,0}}))
```

### Related terms
[Polygon](Polygon.md), [PolygonAngle](PolygonAngle.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of PolygonCoordinates](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/PolygonCoordinates.java)

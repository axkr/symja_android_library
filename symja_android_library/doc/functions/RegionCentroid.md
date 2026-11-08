## RegionCentroid

```
RegionCentroid(reg)
```

> gives the centroid of the region `reg`.

The centroid is the mean of the points of the region: the center of mass of a solid or a plane region of uniform density, of a curve of uniform density along its length, or the mean of the points of a `Point` with several points.

The centroid is known for the region primitives, for example `Point`, `Line`, `Interval`, `Triangle`, `Polygon`, `Rectangle`, `Cuboid`, `Disk`, `Circle`, `Annulus`, `Ball`, `Sphere`, `Ellipsoid`, `Simplex`, `Tetrahedron`, `Parallelepiped`, `Cone` and `Cylinder`, and for mesh regions. Symbolic parameters are allowed. For other expressions the expression stays unevaluated.

See:
* [Wikipedia - Centroid](https://en.wikipedia.org/wiki/Centroid)

### Options

- `Assumptions` - assumptions about the parameters of the region

### Examples

```
>> RegionCentroid(Disk({3, 4}, 2))
{3,4}

>> RegionCentroid(Rectangle({0, 0}, {2, 3}))
{1,3/2}

>> RegionCentroid(Triangle({{0, 0}, {1, 0}, {0, 1}}))
{1/3,1/3}

>> RegionCentroid(Polygon({{0, 0}, {2, 0}, {3, 1}, {1, 1}}))
{3/2,1/2}

>> RegionCentroid(Tetrahedron({{0,0,0},{1,0,0},{0,1,0},{0,0,1}}))
{1/4,1/4,1/4}

>> RegionCentroid(Cone({{0,0,0},{0,0,4}},1))
{0,0,1}
```

The centroid of a curve is weighted by length, the centroid of several points is their mean:

```
>> RegionCentroid(Line({{0,0},{2,0},{2,2}}))
{3/2,1/2}

>> RegionCentroid(Point({{0,0},{2,0},{2,2}}))
{4/3,2/3}
```

Symbolic parameters:

```
>> RegionCentroid(Rectangle({a, b}, {c, d}))
{1/2*(a+c),1/2*(b+d)}

>> RegionCentroid(Disk({a,b},r), Assumptions->r>0)
{a,b}
```

Mesh regions and combinations of regions:

```
>> RegionCentroid(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}))
{1,1}

>> RegionCentroid(RegionDifference(Rectangle({-1, -1}, {1, 1}), Disk({1/2, 0}, 1/4)))
{Pi/(2*(-64+Pi)),0}
```

### Related terms
[Area](Area.md), [ConvexHullMesh](ConvexHullMesh.md), [Mean](Mean.md), [RegionMember](RegionMember.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of RegionCentroid](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/RegionCentroid.java)

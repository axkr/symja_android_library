## RegionMember

```
RegionMember(reg, p)
```

> returns `True` if the point `p` lies in the region `reg`, and `False` if it doesn't.

```
RegionMember(reg)
```

> returns the membership test of the region `reg` as the function object `RegionMemberFunction(reg)`.

For a point with symbolic coordinates the result is the condition which the coordinates have to satisfy, for example `x^2+y^2<=1` for the unit disk. The point needs as many coordinates as the region has; a point of another dimension leaves the expression unevaluated. The boundary belongs to a closed region like a `Disk`.

`RegionMember` knows the region primitives, for example `Point`, `Line`, `InfiniteLine`, `HalfLine`, `Interval`, `Triangle`, `Polygon`, `Rectangle`, `Cuboid`, `Disk`, `Circle`, `Annulus`, `Ball`, `Sphere`, `SphericalShell`, `Ellipsoid`, `Simplex`, `Parallelogram`, `Parallelepiped`, `HalfPlane`, `HalfSpace`, `Cone` and `Cylinder`, sectors of a `Disk` or a `Circle`, `ImplicitRegion`, mesh regions in the plane and in space, and the combinations `RegionUnion`, `RegionIntersection`, `RegionDifference` and `RegionSymmetricDifference`.

### Examples

```
>> RegionMember(Disk({0, 0}, 1), {1/2, 1/2})
True

>> RegionMember(Disk({0, 0}, 1), {1, 0})
True

>> RegionMember(Disk({0, 0}, 1), {2, 0})
False

>> RegionMember(Circle({0, 0}, 1), {0.5, 0.5})
False
```

A point with symbolic coordinates gives the condition of the region:

```
>> RegionMember(Disk(), {x,y})
x^2+y^2<=1

>> RegionMember(Rectangle({0,0},{2,3}), {x,y})
0<=x&&x<=2&&0<=y&&y<=3

>> RegionMember(Annulus({0,0},{1,2}), {x,y})
1<=x^2+y^2&&x^2+y^2<=4

>> RegionMember(Line({{0,0},{1,1}}), {x,y})
x==y&&0<=x+y&&x+y<=2
```

The concave polygon doesn't contain the point `{2,3}` in its notch:

```
>> RegionMember(Polygon({{0, 0}, {4, 0}, {4, 4}, {2, 1}, {0, 4}}), {2, 3})
False
```

A quarter of the unit disk:

```
>> RegionMember(Disk({0,0},1,{0,Pi/2}), {1/2,1/2})
True

>> RegionMember(Disk({0,0},1,{0,Pi/2}), {-1/2,1/2})
False
```

Regions given by a formula, and combinations of regions:

```
>> RegionMember(ImplicitRegion(x^2+y^2<=1, {x,y}), {a,b})
a^2+b^2<=1

>> RegionMember(RegionUnion(Disk(), Disk({3,0},1)), {x,y})
x^2+y^2<=1||(3-x)^2+y^2<=1

>> RegionMember(RegionDifference(Disk({0,0},2), Disk({0,0},1)), {1/2,0})
False

>> RegionMember(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}), {3,1})
False
```

With one argument, `RegionMember` returns a function which can test several points at once:

```
>> RegionMember(Disk())
RegionMemberFunction(Disk({0,0}))

>> RegionMember(Disk())[{{0,0},{2,2}}]
{True,False}
```

### Related terms
[ConstantRegionQ](ConstantRegionQ.md), [Element](Element.md), [RegionCentroid](RegionCentroid.md), [RegionMemberFunction](RegionMemberFunction.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of RegionMember](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/RegionMember.java)

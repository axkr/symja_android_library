## Area

```
Area(reg)
```

> returns the area of the two dimensional region `reg`.

```
Area({x1, ..., xn}, {s, smin, smax}, {t, tmin, tmax})
```

> returns the area of the surface parametrized by `{x1, ..., xn}` over the parameter rectangle.

The area of a region whose [RegionDimension](RegionDimension.md) is not two is `Undefined` - the
area of a point, of a curve and of a solid alike. For a two dimensional region which is embedded in
three dimensions, such as a `Sphere` or a planar `Polygon`, the area is the surface area.

The parametric form integrates the area element of the first fundamental form over the parameter
rectangle, so a part of the surface which the parametrization covers more than once is counted as
often as it is covered. A single scalar `x` describes the graph `{s, t, x}` over the two
parameters.

`Area` accepts the options `AccuracyGoal`, `Assumptions`, `GenerateConditions`, `PerformanceGoal`,
`PrecisionGoal` and `WorkingPrecision`. `Assumptions` refines the symbolic result and
`WorkingPrecision` evaluates it numerically. `AccuracyGoal`, `PrecisionGoal` and `WorkingPrecision`
additionally ask the parametric form for a number, which hands its integral to
[NIntegrate](NIntegrate.md) and so reaches integrands that have no closed form. A closed form
measure of a region primitive never integrates, so the numerical options have no effect on it.

See:
* [Wikipedia - Area](https://en.wikipedia.org/wiki/Area)

### Examples

```
>> Area(Disk({1,2}))
Pi

>> Area(Rectangle({0,0},{3,4}))
12

>> Area(Triangle({{0,0},{1,0},{0,1}}))
1/2

>> Area(Polygon({{0,0},{4,0},{4,3},{0,3}}))
12

>> Area(Annulus({0,0},{1,2}))
3*Pi

>> Area(Simplex(2))
1/2

>> Area(RegularPolygon(6))
3/2*Sqrt(3)
```

A two dimensional region in three dimensions has a surface area:

```
>> Area(Sphere({0,0,0},2))
16*Pi

>> Area(Polygon({{0,0,0},{1,0,0},{0,1,0}}))
1/2

>> Area(Torus())
3/4*Pi^2
```

Every other dimension is `Undefined`:

```
>> Area(Point({1,2}))
Undefined

>> Area(Circle())
Undefined

>> Area(Cuboid())
Undefined
```

`Assumptions` refines a symbolic area, `WorkingPrecision` evaluates it:

```
>> Area(Rectangle({0,0},{a,b}))
Abs(a*b)

>> Area(Rectangle({0,0},{a,b}), Assumptions -> a>0 && b>0)
a*b

>> Area(Disk(), WorkingPrecision -> 20)
3.1415926535897932384
```

The area of a parametrized surface:

```
>> Area({s, t}, {s,0,3}, {t,0,4})
12

>> Area({r*Cos(t), r*Sin(t)}, {r,0,1}, {t,0,2*Pi})
Pi

>> Area({Sin(u)*Cos(v), Sin(u)*Sin(v), Cos(u)}, {u,0,Pi}, {v,0,2*Pi})
4*Pi
```

A parametrization which covers the disk twice gives twice its area:

```
>> Area({r*Cos(t), r*Sin(t)}, {r,0,1}, {t,0,4*Pi})
2*Pi
```

### Related terms
[ArcLength](ArcLength.md), [Perimeter](Perimeter.md), [RegionMeasure](RegionMeasure.md),
[SurfaceArea](SurfaceArea.md), [Volume](Volume.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Area](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Area.java)

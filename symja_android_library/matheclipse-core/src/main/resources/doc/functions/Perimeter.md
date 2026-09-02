## Perimeter

```
Perimeter(reg)
```

> returns the perimeter of the two dimensional region `reg`.

```
Perimeter({x1, x2}, {s, smin, smax}, {t, tmin, tmax})
```

> returns the perimeter of the region parametrized by `{x1, x2}`, which is the total arc length of
> the image of the four edges of the parameter rectangle.

The perimeter is the arc length of the boundary of a two dimensional region. The perimeter of a
region whose [RegionDimension](RegionDimension.md) is not two is `Undefined`.

A smooth closed surface such as a `Sphere` or a `Torus` bounds nothing, and its perimeter is
`Infinity`. A flat `Triangle` or `Polygon` in space is a polytope whose boundary is its edge cycle,
so it keeps a finite perimeter even though it is embedded in three dimensions.

`Perimeter` accepts the options `AccuracyGoal`, `Assumptions`, `GenerateConditions`,
`PerformanceGoal`, `PrecisionGoal` and `WorkingPrecision`. `Assumptions` refines the symbolic
result and `WorkingPrecision` evaluates it numerically; the remaining options only steer a
numerical integration, which the closed form measures do not use, so they are accepted and ignored.

See:
* [Wikipedia - Perimeter](https://en.wikipedia.org/wiki/Perimeter)

### Examples

```
>> Perimeter(Disk({1,2}))
2*Pi

>> Perimeter(Rectangle({0,0},{3,4}))
14

>> Perimeter(Triangle({{0,0},{1,0},{0,1}}))
2+Sqrt(2)

>> Perimeter(Polygon({{0,0},{1,0},{1,1},{0,1}}))
4

>> Perimeter(Annulus({0,0},{1,2}))
6*Pi

>> Perimeter(Simplex(2))
2+Sqrt(2)

>> Perimeter(RegularPolygon(6))
6

>> Perimeter(Parallelogram({0,0},{{1,0},{0,1}}))
4
```

The perimeter of an ellipse is a complete elliptic integral:

```
>> Perimeter(Disk({0,0},{3,1}))
4*EllipticE(-8)

>> Perimeter(Disk({0,0},{1,3}))
12*EllipticE(8/9)
```

A smooth closed surface gives `Infinity`, a flat polytope in space keeps its edge length:

```
>> Perimeter(Sphere())
Infinity

>> Perimeter(Torus())
Infinity

>> Perimeter(Polygon({{0,0,0},{1,0,0},{0,1,0}}))
2+Sqrt(2)

>> Perimeter(Triangle({{0,0,0},{1,0,0},{0,1,1}}))
1+Sqrt(2)+Sqrt(3)
```

Every other dimension is `Undefined`:

```
>> Perimeter(Circle())
Undefined

>> Perimeter(Ball())
Undefined

>> Perimeter(Cuboid())
Undefined
```

`Assumptions` refines a symbolic perimeter:

```
>> Perimeter(Rectangle({0,0},{a,b}), Assumptions -> a>0 && b>0)
2*(a+b)
```

The perimeter of a parametrized region:

```
>> Perimeter({s, t}, {s,0,3}, {t,0,4})
14

>> Perimeter({r*Cos(t), r*Sin(t)}, {r,0,1}, {t,0,2*Pi})
2+2*Pi
```

### Related terms
[Area](Area.md), [ArcLength](ArcLength.md), [RegionBoundary](RegionBoundary.md),
[RegionMeasure](RegionMeasure.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Perimeter](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Perimeter.java)

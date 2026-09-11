## ListInterpolation

```
ListInterpolation({f1, f2, ...})
```

> gives an `InterpolatingFunction` whose value at `i` is `fi`.

```
ListInterpolation({f1, f2, ...}, {{xmin, xmax}})
```

> gives an `InterpolatingFunction` through the values placed evenly from `xmin` to `xmax`.

```
ListInterpolation(matrix)
```

> gives an `InterpolatingFunction` of two variables whose value at `{i, j}` is `matrix[[i, j]]`.

```
ListInterpolation(matrix, {{xmin, xmax}, {ymin, ymax}})
```

> gives an `InterpolatingFunction` of two variables through the values placed evenly over the two ranges.

A matrix is interpolated by a bicubic spline where it has at least 5 values along each axis, and bilinearly otherwise; outside its domain the function takes the value at the nearest edge. A list is interpolated as `Interpolation` interpolates the points `{{x1, f1}, {x2, f2}, ...}`.

See
* [Wikipedia - Bicubic interpolation](https://en.wikipedia.org/wiki/Bicubic_interpolation)

### Options

* `InterpolationOrder -> 1` - interpolates a matrix bilinearly; the default, `3`, uses a bicubic spline

### Examples

```
>> f = ListInterpolation(Table(Sin(x)*Cos(y), {x, 0, 2, 0.25}, {y, 0, 2, 0.25}), {{0, 2}, {0, 2}});

>> Abs(f(1.1, 0.7) - Sin(1.1)*Cos(0.7)) < 10^-3
True

>> g = ListInterpolation({1, 4, 9, 16, 25, 36});

>> g(2.5)
6.25
```

### Related terms
[Interpolation](Interpolation.md), [NDSolve](NDSolve.md)

### Implementation status

* &#x1F9EA; - experimental

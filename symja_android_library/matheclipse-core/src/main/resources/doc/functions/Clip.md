## Clip

```
Clip(expr)
```

> returns `expr` in the range `-1` to `1`. Returns `-1` if `expr` is less than `-1`. Returns `1` if `expr` is greater than `1`.
  
```
Clip(expr, {min, max})
```

> returns `expr` in the range `min` to `max`. Returns `min` if `expr` is less than `min`. Returns `max` if `expr` is greater than `max`.
  
```
Clip(expr, {min, max}, {vMin, vMax})
```

> returns `expr` in the range `min` to `max`. Returns `vMin` if `expr` is less than `min`. Returns `vMax` if `expr` is greater than `max`.

A bound may be `Infinity` or `-Infinity`. `Clip` is not defined for complex values.

See
* [Wikipedia - Clipping (signal processing)](https://en.wikipedia.org/wiki/Clipping_(signal_processing))

### Examples

```
>> Clip(Sin(Pi/7))
Sin(Pi/7)

>> Clip(Tan(E))
Tan(E)

>> Clip(Tan(2*E))
-1

>> Clip(Tan(-2*E))
1

>> Clip(x)
Clip(x)

>> Clip(Tan(2*E), {-1/2,1/2})
-1/2

>> Clip(Tan(-2*E), {-1/2,1/2})
1/2

>> Clip(Tan(E), {-1/2,1/2}, {a,b})
Tan(E)

>> Clip(Tan(2*E), {-1/2,1/2}, {a,b})
a

>> Clip(Tan(-2*E), {-1/2,1/2}, {a,b})
b
```

A bound may be infinite, which is how a one sided range is written - `Clip(expr, {0, Infinity})` rejects negative values:

```
>> Clip(-5, {0, Infinity})
0

>> Clip({-5, 1, 3.5}, {0, Infinity})
{0,1,3.5}

>> Clip(5, {-Infinity, 0})
0
```

`Clip` maps over a list in its first argument, and over the bounds of an `Interval` or `IntervalData`. A list holding an element that cannot be clipped stays unevaluated as a whole:

```
>> Clip({-2, 0, 2})
{-1,0,1}

>> Clip(Interval({-3, 5}))
Interval({-1,1})

>> Clip(IntervalData({-3, Less, Less, 5}))
IntervalData({-1,LessEqual,LessEqual,1})
```

`Clip` saturates, so a bound that is excluded from the domain of an `IntervalData` can still be attained in the image - every value of `(-5,-1)` is clipped onto `-1`:

```
>> Clip(IntervalData({-5, Less, Less, -1}))
IntervalData({-1,LessEqual,LessEqual,-1})
```

```
>> PiecewiseExpand(Clip(x)) 
Piecewise({{-1,x<-1},{1,x>1}},x)

>> PiecewiseExpand(Clip(x, {-7, 5}, {a, b})) 
Piecewise({{a,x<-7},{b,x>5}},x)
```

### Related terms 
[Piecewise](Piecewise.md), [PiecewiseExpand](PiecewiseExpand.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Clip](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PiecewiseFunctions.java#L155) 

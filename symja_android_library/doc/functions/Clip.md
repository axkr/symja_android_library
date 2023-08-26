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

>> PiecewiseExpand(Clip(x)) 
Piecewise({{-1,x<-1},{1,x>1}},x)
```

### Related terms 
[Piecewise](Piecewise.md), [PiecewiseExpand](PiecewiseExpand.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Clip](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PiecewiseFunctions.java#L155) 

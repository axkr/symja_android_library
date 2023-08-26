## Ramp

```
Ramp(z)
```

> The `Ramp` function is a unary real function, whose graph is shaped like a ramp.  
 
See
* [Wikipedia - Ramp function](https://en.wikipedia.org/wiki/Ramp_function)

### Examples

```
>> PiecewiseExpand(Ramp(x)) 
Piecewise({{x,x>=0},0})
```

### Related terms 
[Piecewise](Piecewise.md), [PiecewiseExpand](PiecewiseExpand.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Ramp](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PiecewiseFunctions.java#L704) 

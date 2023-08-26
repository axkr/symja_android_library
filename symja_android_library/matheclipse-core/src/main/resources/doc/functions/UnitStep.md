## UnitStep

```
UnitStep(expr)
```

> returns `0`, if `expr` is less than `0` and returns `1`, if `expr` is greater equal than `0`.

See
* [Wikipedia - Heaviside step function](https://en.wikipedia.org/wiki/Heaviside_step_function)  
* [Wikipedia - Step function](https://en.wikipedia.org/wiki/Step_function)

### Examples

```
>> UnitStep(-42)
0
```

### Related terms 
[Piecewise](Piecewise.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of UnitStep](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PiecewiseFunctions.java#L1030) 

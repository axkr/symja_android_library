## PiecewiseExpand
 

```
PiecewiseExpand(function)
```

> expands piecewise expressions into a `Piecewise` function. Currently only `Abs, Clip, If, Ramp, UnitStep` are converted to Piecewise expressions.

```
PiecewiseExpand(function, Reals)
```

> expands `function`  piecewise under the assumption, that all variables are real numbers.

See:

* [Wikipedia - Piecewise](http://en.wikipedia.org/wiki/Piecewise)
* [Wikipedia - Step function](https://en.wikipedia.org/wiki/Step_function)
* [Wikipedia - Heaviside step function](https://en.wikipedia.org/wiki/Heaviside_step_function)  


### Examples

```
>> PiecewiseExpand(Abs(x), Reals) 
Piecewise({{-x,x<0},x})
```
 

### Related terms 
[Abs](Abs.md), [Clip](Clip.md), [If](If.md), [Piecewise](Piecewise.md), [Ramp](Ramp.md), [UnitStep](UnitStep.md)  






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PiecewiseExpand](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PiecewiseFunctions.java#L640) 

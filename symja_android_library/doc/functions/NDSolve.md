## NDSolve

```
NDSolve({equation-list}, functions, t)
```
> attempts to solve the linear differential `equation-list` for the `functions` and the time-dependent-variable `t`. Returns an `InterpolatingFunction` function object.

See:  
* [Wikipedia - Ordinary differential equation](https://en.wikipedia.org/wiki/Ordinary_differential_equation)

### Examples

Example taken from [Tutorial — Differential Equations](https://socialinnovationsimulation.com/2013/07/19/tutorial-differential-equations-2/)

```
>> model=NDSolve({x'(t) == 10*(y(t) - x(t)), y'(t) == x(t)*(28 - z(t)) - y(t), z'(t) == x(t)*y(t) - 8/3*z(t), x(0)== 0, y(0) == 1, z(0) == 0}, {x, y, z}, {t, 0, 20});

{{x->InterpolatingFunction[Piecewise({{InterpolatingPolynomial({........
```

Plot the interpolating function and the sine function.

```
>> Plot({Evaluate(z(t) /.model)}, {t, 0, 20})
```

### Related terms
[DSolve](DSolve.md), [InterpolatingFunction](InterpolatingFunction.md), [NRoots](NRoots.md), [Solve](Solve.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NDSolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NDSolve.java#L27) 

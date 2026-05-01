## DSolveValue

```
DSolveValue(equation, f(var), var)
```

> attempts to solve a linear differential `equation` for the function `f(var)` and variable `var`.

See:  
* [Wikipedia - Ordinary differential equation](https://en.wikipedia.org/wiki/Ordinary_differential_equation)

### Examples

```
>> DSolveValue({y'(x)==y(x)+2},y(x), x)
-2+E^x*C(1)

>> DSolveValue({y'(x)==y(x)+2,y(0)==1},y(x), x)
2+3*E^x
```

### Related terms
[DSolve](DSolve.md), [Factor](Factor.md), [FindRoot](FindRoot.md), [NRoots](NRoots.md),[Solve](Solve.md)
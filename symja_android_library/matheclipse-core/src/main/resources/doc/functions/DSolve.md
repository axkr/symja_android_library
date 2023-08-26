## DSolve

```
DSolve(equation, f(var), var)
```
> attempts to solve a linear differential `equation` for the function `f(var)` and variable `var`.

See:  
* [Wikipedia - Ordinary differential equation](https://en.wikipedia.org/wiki/Ordinary_differential_equation)

### Examples

```
>> DSolve({y'(x)==y(x)+2},y(x), x)
{{y(x)->-2+E^x*C(1)}}

>> DSolve({y'(x)==y(x)+2,y(0)==1},y(x), x)
{{y(x)->-2+3*E^x}}
```

### Related terms
[Factor](Factor.md), [FindRoot](FindRoot.md), [NRoots](NRoots.md),[Solve](Solve.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DSolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/DSolve.java#L59) 

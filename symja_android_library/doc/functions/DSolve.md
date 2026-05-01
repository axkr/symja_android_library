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

>> DSolve(y''(x) == 0, y(x), x)
{{y(x)->C(1)+x*C(2)}}     

>> DSolve(y''(x) == y(x), y(x), x)
{{y(x)->C(1)/E^x+E^x*C(2)}}

>> DSolve(y''(x) == y(x), y, x)
{{y->Function({x},C(1)/E^x+E^x*C(2))}}        
```

DSolve can also solve basic PDE

```
>> DSolve(D(f(x, y), x)/f(x, y) + 3*D(f(x, y), y) / f(x, y) == 2, f, {x, y}) 
{{f->Function({x,y},E^(2*x)*C(1)[-3*x+y])}}

>> DSolve(D(f(x, y), x)*x + D(f(x, y), y)*y == 2, f(x, y), {x, y}) 
{{f(x,y)->2*Log(x)+C(1)[y/x]}}
        
>> DSolve(D(y(x, t), t) + 2*D(y(x, t), x) == 0, y(x, t), {x, t}) 
{{y(x,t)->C(1)[1/2*(2*t-x)]}}
```

### Related terms
[DSolveValue](DSolveValue.md), [Factor](Factor.md), [FindRoot](FindRoot.md), [NRoots](NRoots.md),[Solve](Solve.md)






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DSolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/DSolve.java#L60) 

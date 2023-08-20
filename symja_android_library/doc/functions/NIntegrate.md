## NIntegrate
```
NIntegrate(f, {x,a,b})
```
> computes the numerical univariate real integral of `f` with respect to `x` from `a` to `b`.


See: 
* [Wikipedia - Numerical integration](https://en.wikipedia.org/wiki/Numerical_integration)
* [Wikipedia - Trapezoidal rule](https://en.wikipedia.org/wiki/Trapezoidal_rule)
* [Wikipedia - Romberg's method](https://en.wikipedia.org/wiki/Romberg%27s_method)
* [Wikipedia - Riemann sum](https://en.wikipedia.org/wiki/Riemann_sum)
* [Wikipedia - Truncation error (numerical integration)](https://en.wikipedia.org/wiki/Truncation_error_(numerical_integration))

### Examples
```   
>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1})
-0.0208333333333333
```

LegendreGauss is the default method for numerical integration

```
>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->LegendreGauss)
-0.0208333333333333

>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Simpson)
-0.0208333320915699

>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Trapezoid)
-0.0208333271245165

>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Romberg)
-0.0208333333333333
```

Other options include `MaxIterations` and `MaxPoints`

```
>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Trapezoid, MaxIterations->5000)
-0.0208333271245165
```

### Related terms 
[D](D.md), [DSolve](DSolve.md), [Integrate](Integrate.md), [Limit](Limit.md), [ND](ND.md)

		






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NIntegrate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NIntegrate.java#L79) 

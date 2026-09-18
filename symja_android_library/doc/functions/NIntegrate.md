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
* [Wikipedia - Simpson's rule](https://en.wikipedia.org/wiki/Simpson%27s_rule)
* [Wikipedia - Truncation error (numerical integration)](https://en.wikipedia.org/wiki/Truncation_error_(numerical_integration))
* [Wikipedia - Gauss-Kronrod quadrature formula)](https://en.wikipedia.org/wiki/Gauss%E2%80%93Kronrod_quadrature_formula)

### Examples
```   
>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1})
-0.0208333333333333
```

With `Method->Automatic` the integral is computed in these steps:

1. The interval is split where the symbolic form of the integrand has a kink, a jump or a pole: at the real zeros and poles of rational arguments of `Abs`, `RealAbs`, `Sign`, `UnitStep`, `HeavisideTheta`, `Max`, `Min`, `Clip` and of the conditions of `Piecewise`, where linear arguments of `Floor`, `Ceiling`, `Round`, `IntegerPart`, `FractionalPart` cross an integer, and at the real poles of a rational denominator. An infinite interval `(-Infinity, Infinity)` is split at `0`.
2. An integrand `g(x)*Sin(w*x+c)` or `g(x)*Cos(w*x+c)` on an infinite interval, with `g(x)` tending to `0`, is integrated from one zero of the oscillating factor to the next, and the series of these half periods is summed with Wynn's epsilon algorithm.
3. Otherwise every piece is integrated with the globally adaptive Gauss-Kronrod rule of QUADPACK (`QAGS`, and `QAGI` for an infinite piece), whose epsilon extrapolation handles integrable endpoint singularities. A piece which stops at a point where the integrand cannot be evaluated is split there. A piece which fails for another reason than divergence is tried again with the `DoubleExponential` (tanh-sinh) rule.
4. If that fails too, the integral is tried symbolically with `Integrate`. Otherwise it stays unevaluated with the message `NIntegrate::ncvb` (no convergence, e.g. a divergent integral) or `NIntegrate::slwcon` (converging too slowly).

```
>> NIntegrate(Abs(x^2-2*x), {x,-10,10})
669.3333

>> NIntegrate(1/Sqrt(x), {x,0,1})
2.0

>> NIntegrate(Sin(x)/x, {x,0,Infinity})
1.5708

>> NIntegrate(1/x, {x,0,1})
NIntegrate(1/x,{x,0,1})
```

The methods `GaussKronrod` or `GlobalAdaptive` (the QUADPACK rule), `DoubleExponential`, `ClenshawCurtisRule`, `GaussLobattoRule`, `NewtonCotesRule`, `Romberg`, `Simpson`, `Trapezoid` and `LegendreGauss` can be selected explicitly; they are used as requested, without switching. `LegendreGauss` is a fixed-order rule without a convergence test, it returns a finite number even for a divergent integral.

```
>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->LegendreGauss)
-0.0208333333333333

>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Simpson)
-0.0208333320915699

>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Trapezoid)
-0.0208333271245165

>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Romberg)
-0.0208333333333333

>> NIntegrate(Exp(-x^2),{x,-Infinity,Infinity}, Method->GaussKronrod) 
1.772453850905516

>> NIntegrate(Cos(200*x),{x,0,1}, Method->GaussKronrod) 
-0.004366486486070
```

Other options include `MaxIterations` (the maximum number of integrand evaluations of an adaptive rule), `MaxPoints` (the number of points of the `LegendreGauss` rule), `PrecisionGoal` (`10^(-PrecisionGoal)` is the relative error tolerance, and the result is rounded to that many decimal places) and `AccuracyGoal` (`10^(-AccuracyGoal)` is the absolute error tolerance, `Infinity` for none)

```
>> NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Trapezoid, MaxIterations->5000)
-0.0208333271245165
```

Integrate along a complex line:

```
>> NIntegrate(1.25+I*2.0+(-3.25+I*0.125)*x+(I*3.0)*x^2,{x, -1.75+I*4.0, 1.5+I*(-12.0)})
-1427.4921875+I*(-709.06640625)
```

### Related terms 
[D](D.md), [DSolve](DSolve.md), [Integrate](Integrate.md), [Limit](Limit.md), [ND](ND.md)

		






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of NIntegrate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NIntegrate.java#L121) 

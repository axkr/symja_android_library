## GammaRegularized

```
GammaRegularized(a, z)
```

> returns the regularized incomplete gamma function `Q(a, z) == Gamma(a, z)/Gamma(a)`.

```
GammaRegularized(a, z0, z1)
```

> returns the generalized regularized incomplete gamma function `Q(a, z0) - Q(a, z1) == (Gamma(a, z0) - Gamma(a, z1))/Gamma(a)`.

`GammaRegularized(a, z)` is the upper tail: the integral of `t^(a-1)*E^(-t)/Gamma(a)` from `z` to `Infinity`. `GammaRegularized(a, 0, z)` is the lower tail `P(a, z)`, so `GammaRegularized(a, 0, z) + GammaRegularized(a, z) == 1`. It is the `CDF` of the `GammaDistribution`, and `GammaRegularized(k+1, m)` the `CDF` of the `PoissonDistribution` at `k`.

For a positive integer `a` and for `a == 1/2`, `GammaRegularized(a, z)` is written in elementary functions and `Erfc`. At `a == 0` and at a negative integer `a`, the pole of `Gamma(a)` makes it `0`. `GammaRegularized(a, 0)` is `1` if `Re(a) > 0`. With numbers, one of them inexact, or in `N(..., n)`, both forms evaluate numerically, also for complex arguments. `FunctionExpand` writes them with `Gamma`.

`GammaRegularized` is `Listable`. The inverse function is [InverseGammaRegularized](InverseGammaRegularized.md).

See
* [Wikipedia - Incomplete gamma function, regularized gamma functions](https://en.wikipedia.org/wiki/Incomplete_gamma_function#Regularized_gamma_functions_and_Poisson_random_variables)
* [NIST - Incomplete gamma functions, definitions](https://dlmf.nist.gov/8.2)

### Examples

```
>> GammaRegularized(3, z)
(1+z+z^2/2)/E^z

>> GammaRegularized(3, 2)
5/E^2

>> GammaRegularized(1/2, z)
Erfc(Sqrt(z))

>> GammaRegularized(1.5, 2.0)
0.261464

>> N(GammaRegularized(3/2, 2), 30)
0.261464129949110622202822075976

>> GammaRegularized(1.5+I, 2.0)
0.223876+I*0.307319

>> GammaRegularized(5/2, 0)
1
```

The generalized function, and the lower tail `GammaRegularized(a, 0, z)`:

```
>> GammaRegularized(1, z0, z1)
E^(-z0)-1/E^z1

>> GammaRegularized(2.0, 1.0, 3.0)
0.536611

>> GammaRegularized(1.5, 0, 2.0) + GammaRegularized(1.5, 2.0)
1.0
```

```
>> D(GammaRegularized(a, z), z)
-1/(E^z*z^(1-a)*Gamma(a))

>> FunctionExpand(GammaRegularized(a, z))
Gamma(a,z)/Gamma(a)

>> CDF(GammaDistribution(a, b), x)
Piecewise({{GammaRegularized(a,0,x/b),x>0}},0)
```

### Related terms
[BetaRegularized](BetaRegularized.md), [Erfc](Erfc.md), [Gamma](Gamma.md), [GammaDistribution](GammaDistribution.md), [InverseGammaRegularized](InverseGammaRegularized.md), [PoissonDistribution](PoissonDistribution.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of GammaRegularized](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java)

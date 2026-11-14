## InverseGammaRegularized

```
InverseGammaRegularized(a, s)
```

> returns the inverse of the regularized incomplete gamma function: the solution `z` of `GammaRegularized(a, z) == s`.

```
InverseGammaRegularized(a, z0, s)
```

> returns the solution `z` of `GammaRegularized(a, z0, z) == s`.

`GammaRegularized(a, z)` is the upper tail of the gamma distribution, so `InverseGammaRegularized(a, s)` is the point above which a share `s` of the probability lies. The lower tail is inverted by `InverseGammaRegularized(a, 0, s)`: it is the `Quantile` of `GammaDistribution(a, 1)` at `s`.

For a positive `a`, `InverseGammaRegularized(a, 0)` is `Infinity` and `InverseGammaRegularized(a, 1)` is `0`; `InverseGammaRegularized(1, s)` is `-Log(s)` for a number `0 < s < 1`. `InverseGammaRegularized(a, Infinity, s)` is `InverseGammaRegularized(a, -s)`. Real numbers, one of them inexact, or in `N(..., n)`, evaluate numerically. Outside the real domain, for example for `s > 1` in the two argument form, the expression stays unevaluated.

`InverseGammaRegularized` is `Listable`.

See
* [Wikipedia - Incomplete gamma function, regularized gamma functions](https://en.wikipedia.org/wiki/Incomplete_gamma_function#Regularized_gamma_functions_and_Poisson_random_variables)
* [NIST - Incomplete gamma functions, definitions](https://dlmf.nist.gov/8.2)

### Examples

```
>> InverseGammaRegularized(1.5, 0.3)
1.83244

>> GammaRegularized(1.5, InverseGammaRegularized(1.5, 0.3))
0.3

>> N(InverseGammaRegularized(3/2, 1/3), 30)
1.70235310327255415644065748658

>> InverseGammaRegularized(1, 1/3)
Log(3)

>> {InverseGammaRegularized(3/2, 0), InverseGammaRegularized(3/2, 1)}
{Infinity,0}

>> InverseGammaRegularized(2.0, 1.5)
InverseGammaRegularized(2.0,1.5)
```

The three argument form, and the quantile of the gamma distribution:

```
>> InverseGammaRegularized(1.5, 0.5, 0.3)
1.17966

>> GammaRegularized(1.5, 0.5, InverseGammaRegularized(1.5, 0.5, 0.3))
0.3

>> Quantile(GammaDistribution(3/2, 1), 3/10)
InverseGammaRegularized(3/2,0,3/10)

>> N(Quantile(GammaDistribution(3/2, 1), 3/10))
0.711826
```

```
>> D(InverseGammaRegularized(a, s), s)
-E^InverseGammaRegularized(a,s)*Gamma(a)*InverseGammaRegularized(a,s)^(1-a)
```

### Related terms
[Gamma](Gamma.md), [GammaDistribution](GammaDistribution.md), [GammaRegularized](GammaRegularized.md), [InverseBetaRegularized](InverseBetaRegularized.md), [Quantile](Quantile.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of InverseGammaRegularized](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java)

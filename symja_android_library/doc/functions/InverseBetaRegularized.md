## InverseBetaRegularized

```
InverseBetaRegularized(s, a, b)
```

> returns the inverse of the regularized incomplete beta function: the solution `z` of `BetaRegularized(z, a, b) == s`.

```
InverseBetaRegularized(z0, s, a, b)
```

> returns the solution `z` of `BetaRegularized(z0, z, a, b) == s`.

`InverseBetaRegularized(s, a, b)` is the `Quantile` of the beta distribution with the shape parameters `a` and `b` at `s`.

For a positive `a`, `InverseBetaRegularized(0, a, b)` is `0` and `InverseBetaRegularized(1, a, b)` is `1`. `InverseBetaRegularized(z0, 0, a, b)` is `z0`, and `InverseBetaRegularized(0, s, a, b)` is `InverseBetaRegularized(s, a, b)`. Real numbers, one of them inexact, or in `N(..., n)`, evaluate numerically. Outside the real domain, for example for `s > 1`, the expression stays unevaluated.

`InverseBetaRegularized` is `Listable`.

See
* [Wikipedia - Beta function, incomplete beta function](https://en.wikipedia.org/wiki/Beta_function#Incomplete_beta_function)
* [NIST - Incomplete beta functions, definitions](https://dlmf.nist.gov/8.17)

### Examples

```
>> InverseBetaRegularized(0.3, 1.5, 2.5)
0.229832

>> BetaRegularized(InverseBetaRegularized(0.3, 1.5, 2.5), 1.5, 2.5)
0.3

>> N(InverseBetaRegularized(1/3, 3/2, 5/2), 30)
0.25

>> {InverseBetaRegularized(0, 3/2, 5/2), InverseBetaRegularized(1, 3/2, 5/2)}
{0,1}

>> InverseBetaRegularized(1.5, 2.0, 3.0)
InverseBetaRegularized(1.5,2.0,3.0)
```

The value `0.25` above is exact, as `BetaRegularized(1/4, 3/2, 5/2) == 1/3`:

```
>> N(BetaRegularized(1/4, 3/2, 5/2), 30)
0.333333333333333333333333333333
```

The four argument form:

```
>> InverseBetaRegularized(0.1, 0.3, 1.5, 2.5)
0.289127

>> BetaRegularized(0.1, InverseBetaRegularized(0.1, 0.3, 1.5, 2.5), 1.5, 2.5)
0.3

>> InverseBetaRegularized(z0, 0, a, b)
z0
```

```
>> D(InverseBetaRegularized(s, a, b), s)
Beta(a,b)*(1-InverseBetaRegularized(s,a,b))^(1-b)*InverseBetaRegularized(s,a,b)^(
1-a)
```

### Related terms
[Beta](Beta.md), [BetaRegularized](BetaRegularized.md), [InverseGammaRegularized](InverseGammaRegularized.md), [Quantile](Quantile.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of InverseBetaRegularized](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java)

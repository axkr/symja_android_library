## BetaRegularized

```
BetaRegularized(z, a, b)
```

> returns the regularized incomplete beta function `I(z; a, b) == Beta(z, a, b)/Beta(a, b)`.

```
BetaRegularized(z0, z1, a, b)
```

> returns the generalized regularized incomplete beta function `I(z1; a, b) - I(z0; a, b) == (Beta(z1, a, b) - Beta(z0, a, b))/Beta(a, b)`.

`BetaRegularized(z, a, b)` is the integral of `t^(a-1)*(1-t)^(b-1)/Beta(a, b)` from `0` to `z`. For `0 <= z <= 1` and positive `a`, `b` it is the `CDF` of the beta distribution. `BetaRegularized(1-p, n-k, k+1)` is the `CDF` of the `BinomialDistribution` at `k`. The symmetry `BetaRegularized(z, a, b) == 1 - BetaRegularized(1-z, b, a)` holds.

For a positive integer `b` it is `z^a` times a polynomial in `1-z`, for example `BetaRegularized(z, a, 1) == z^a`, and `BetaRegularized(z, 1, b) == 1-(1-z)^b`. With numbers, one of them inexact, or in `N(..., n)`, it evaluates numerically, also for `z` outside `[0, 1]` and for complex `z`. `FunctionExpand` writes it with `Beta` and `Gamma`.

`BetaRegularized` is `Listable`. The inverse function is [InverseBetaRegularized](InverseBetaRegularized.md).

See
* [Wikipedia - Beta function, incomplete beta function](https://en.wikipedia.org/wiki/Beta_function#Incomplete_beta_function)
* [NIST - Incomplete beta functions, definitions](https://dlmf.nist.gov/8.17)

### Examples

```
>> BetaRegularized(z, 2, 3)
(1+2*(1-z)+3*(1-z)^2)*z^2

>> BetaRegularized(1/3, 2, 3)
11/27

>> BetaRegularized(z, a, 1)
z^a

>> BetaRegularized(0.3, 1.5, 2.5)
0.415688

>> N(BetaRegularized(1/3, 3/2, 5/2), 30)
0.469631665772579399638588822643
```

The generalized function:

```
>> BetaRegularized(1/4, 3/4, 2, 3)
11/16

>> BetaRegularized(0.25, 0.75, 2.0, 3.0)
0.6875
```

The symmetry:

```
>> Chop(BetaRegularized(0.3, 1.5, 2.5) - (1 - BetaRegularized(0.7, 2.5, 1.5)))
0
```

```
>> D(BetaRegularized(z, a, b), z)
1/((1-z)^(1-b)*z^(1-a)*Beta(a,b))

>> FunctionExpand(BetaRegularized(z, a, b))
(Beta(z,a,b)*Gamma(a+b))/(Gamma(a)*Gamma(b))

>> CDF(BinomialDistribution(n, p), k)
Piecewise({{BetaRegularized(1-p,n-Floor(k),1+Floor(k)),0<=k&&k<n},{1,k>=n}},0)
```

### Related terms
[Beta](Beta.md), [BinomialDistribution](BinomialDistribution.md), [GammaRegularized](GammaRegularized.md), [InverseBetaRegularized](InverseBetaRegularized.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of BetaRegularized](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java)

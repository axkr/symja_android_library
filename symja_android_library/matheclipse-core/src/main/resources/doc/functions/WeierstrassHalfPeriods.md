## WeierstrassHalfPeriods

```
WeierstrassHalfPeriods({g2, g3})
```

> returns the half-periods `{w1, w3}` of the Weierstrass elliptic functions with the invariants `g2` and `g3`.

The functions [WeierstrassP](WeierstrassP.md) and [WeierstrassPPrime](WeierstrassPPrime.md) are periodic with the periods `2*w1` and `2*w3`. Of the pairs which generate the same lattice of periods, the one is returned whose quotient `w3/w1` lies in the fundamental domain, as in Mathematica.

At the half-period `w1` the function `WeierstrassP` takes the value of a root of `4*t^3 - g2*t - g3`, and `WeierstrassPPrime` is zero. [WeierstrassInvariants](WeierstrassInvariants.md) is the inverse function.

The half-periods are computed for numbers: with machine precision for machine numbers, and to the requested precision with `N(..., n)`. For exact numbers the expression stays unevaluated.

See
* [Wikipedia - Weierstrass's elliptic functions](https://en.wikipedia.org/wiki/Weierstrass%27s_elliptic_functions)
* [NIST - Weierstrass elliptic functions, definitions and periodic properties](https://dlmf.nist.gov/23.2)

### Examples

```
>> WeierstrassHalfPeriods({2, 3})
WeierstrassHalfPeriods({2,3})

>> N(WeierstrassHalfPeriods({2, 3}), 30)
{1.19722088977836856774783705885,0.598610444889184283873918529426+I*1.17514061463977117341504128809}

>> WeierstrassHalfPeriods({2.0+I, 3.0})
{1.1959+I*(-0.0364251),0.559063+I*1.15384}
```

At `w1` the function `WeierstrassP` is the real root of `4*t^3 - 2*t - 3`, and `WeierstrassPPrime` is zero to the precision:

```
>> With({w = N(WeierstrassHalfPeriods({2, 3}), 30)}, {WeierstrassP(w[[1]], {2, 3}), WeierstrassPPrime(w[[1]], {2, 3})})
{1.08999053607907864250921114912,1.03617939868204896947599649098*10^-32}

>> x /. NSolve(4*x^3 - 2*x - 3 == 0, x)
{-0.544995+I*(-0.625348),-0.544995+I*0.625348,1.08999}
```

`WeierstrassP` is periodic with the period `2*w3`:

```
>> With({w = WeierstrassHalfPeriods({2.0, 3.0})}, Chop(WeierstrassP(0.3+0.2*I + 2*w[[2]], {2.0, 3.0}) - WeierstrassP(0.3+0.2*I, {2.0, 3.0})))
0
```

`WeierstrassInvariants` is the inverse function:

```
>> WeierstrassHalfPeriods(WeierstrassInvariants({1.0, 2.0*I}))
{1.0,I*2.0}
```

### Related terms
[WeierstrassInvariants](WeierstrassInvariants.md), [WeierstrassP](WeierstrassP.md), [WeierstrassPPrime](WeierstrassPPrime.md), [WeierstrassSigma](WeierstrassSigma.md), [WeierstrassZeta](WeierstrassZeta.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of WeierstrassHalfPeriods](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

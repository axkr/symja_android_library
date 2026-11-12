## WeierstrassInvariants

```
WeierstrassInvariants({w1, w3})
```

> returns the invariants `{g2, g3}` of the Weierstrass elliptic functions with the half-periods `w1` and `w3`.

The invariants are the lattice sums `g2 == 60*Sum(1/p^4)` and `g3 == 140*Sum(1/p^6)` over the nonzero periods `p == 2*m*w1 + 2*n*w3` with integers `m` and `n`.

They are the second argument of [WeierstrassP](WeierstrassP.md) and the other Weierstrass functions, and [WeierstrassHalfPeriods](WeierstrassHalfPeriods.md) is the inverse function.

The invariants are computed for numbers: with machine precision for machine numbers, and to the requested precision with `N(..., n)`. For exact numbers the expression stays unevaluated.

See
* [Wikipedia - Weierstrass's elliptic functions](https://en.wikipedia.org/wiki/Weierstrass%27s_elliptic_functions)
* [NIST - Weierstrass elliptic functions, lattice invariants](https://dlmf.nist.gov/23.3)

### Examples

```
>> WeierstrassInvariants({1.0, 2.0*I})
{8.12422,4.44305}

>> WeierstrassInvariants({1.5, 0.5+I})
{-1.67585+I*4.36539,2.46941+I*(-1.30117)}
```

```
>> WeierstrassInvariants({1, 2*I})
WeierstrassInvariants({1,I*2})

>> N(WeierstrassInvariants({1, 2*I}), 30)
{8.12421844305301709071748188236,4.44305204494594999062854967808}
```

`WeierstrassHalfPeriods` is the inverse function:

```
>> WeierstrassHalfPeriods(WeierstrassInvariants({1.0, 2.0*I}))
{1.0,I*2.0}
```

### Related terms
[WeierstrassHalfPeriods](WeierstrassHalfPeriods.md), [WeierstrassP](WeierstrassP.md), [WeierstrassPPrime](WeierstrassPPrime.md), [WeierstrassSigma](WeierstrassSigma.md), [WeierstrassZeta](WeierstrassZeta.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of WeierstrassInvariants](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

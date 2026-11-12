## WeierstrassSigma

```
WeierstrassSigma(u, {g2, g3})
```

> returns the Weierstrass sigma function of `u` for the invariants `g2` and `g3`.

It is the entire function with the logarithmic derivative [WeierstrassZeta](WeierstrassZeta.md) and `WeierstrassSigma(u, {g2, g3}) == u + ...` near `u == 0`, so that

```
D(WeierstrassSigma(u, {g2, g3}), u) == WeierstrassSigma(u, {g2, g3})*WeierstrassZeta(u, {g2, g3})
```

At `u == 0` its value is `0`. For `{g2, g3} == {0, 0}` it is `u`, and for exact invariants with `g2^3 == 27*g3^2` (and `g2 != 0`) it is an elementary function. For numbers it evaluates numerically with machine precision if one of them is a machine number, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Weierstrass's elliptic functions](https://en.wikipedia.org/wiki/Weierstrass%27s_elliptic_functions)
* [NIST - Weierstrass elliptic functions, definitions and periodic properties](https://dlmf.nist.gov/23.2)

### Examples

```
>> WeierstrassSigma(0.5, {2, 3})
0.499712

>> WeierstrassSigma(0.5+0.25*I, {2, 3})
0.50037+I*0.249673
```

```
>> WeierstrassSigma(1/2, {2, 3})
WeierstrassSigma(1/2,{2,3})

>> N(WeierstrassSigma(1/2, {2, 3}), 30)
0.499711631749231065733444045802
```

Closed forms, and the derivative:

```
>> WeierstrassSigma(z, {0, 0})
z

>> WeierstrassSigma(z, {12, 8})
(E^(z^2/2)*Sin(Sqrt(3)*z))/Sqrt(3)

>> WeierstrassSigma(0, {2, 3})
0

>> D(WeierstrassSigma(z, {g2, g3}), z)
WeierstrassSigma(z,{g2,g3})*WeierstrassZeta(z,{g2,g3})
```

### Related terms
[WeierstrassHalfPeriods](WeierstrassHalfPeriods.md), [WeierstrassP](WeierstrassP.md), [WeierstrassPPrime](WeierstrassPPrime.md), [WeierstrassZeta](WeierstrassZeta.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of WeierstrassSigma](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

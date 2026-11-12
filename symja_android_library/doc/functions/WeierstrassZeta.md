## WeierstrassZeta

```
WeierstrassZeta(u, {g2, g3})
```

> returns the Weierstrass zeta function of `u` for the invariants `g2` and `g3`.

Its derivative is `-WeierstrassP(u, {g2, g3})`, and it has a simple pole with residue `1` at `u == 0`. It is not elliptic but quasi-periodic: for a half-period `w` of [WeierstrassHalfPeriods](WeierstrassHalfPeriods.md)

```
WeierstrassZeta(u + 2*w, {g2, g3}) == WeierstrassZeta(u, {g2, g3}) + 2*WeierstrassZeta(w, {g2, g3})
```

At `u == 0` its value is `ComplexInfinity`. For `{g2, g3} == {0, 0}` it is `1/u`, and for exact invariants with `g2^3 == 27*g3^2` (and `g2 != 0`), where two roots of the cubic `4*t^3 - g2*t - g3` are equal, it is an elementary function. For numbers it evaluates numerically with machine precision if one of them is a machine number, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Weierstrass's elliptic functions](https://en.wikipedia.org/wiki/Weierstrass%27s_elliptic_functions)
* [NIST - Weierstrass elliptic functions, definitions and periodic properties](https://dlmf.nist.gov/23.2)

### Examples

```
>> WeierstrassZeta(0.5, {2, 3})
1.99516

>> WeierstrassZeta(0.5+0.25*I, {2, 3})
1.59976+I*(-0.806585)
```

```
>> WeierstrassZeta(1/2, {2, 3})
WeierstrassZeta(1/2,{2,3})

>> N(WeierstrassZeta(1/2, {2, 3}), 30)
1.99515929396080016162595754613
```

The quasi-periodicity:

```
>> Chop(WeierstrassZeta(0.5, {2.0, 3.0}) - (WeierstrassZeta(0.5 + 2*WeierstrassHalfPeriods({2.0, 3.0})[[1]], {2.0, 3.0}) - 2*WeierstrassZeta(WeierstrassHalfPeriods({2.0, 3.0})[[1]], {2.0, 3.0})))
0
```

Closed forms, and the derivative:

```
>> WeierstrassZeta(z, {0, 0})
1/z

>> WeierstrassZeta(z, {12, 8})
z+Sqrt(3)*Cot(Sqrt(3)*z)

>> WeierstrassZeta(0, {2, 3})
ComplexInfinity

>> D(WeierstrassZeta(z, {g2, g3}), z)
-WeierstrassP(z,{g2,g3})
```

### Related terms
[WeierstrassHalfPeriods](WeierstrassHalfPeriods.md), [WeierstrassP](WeierstrassP.md), [WeierstrassPPrime](WeierstrassPPrime.md), [WeierstrassSigma](WeierstrassSigma.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of WeierstrassZeta](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

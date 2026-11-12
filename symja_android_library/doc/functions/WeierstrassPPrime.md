## WeierstrassPPrime

```
WeierstrassPPrime(u, {g2, g3})
```

> returns the derivative of the Weierstrass elliptic function [WeierstrassP](WeierstrassP.md) with respect to `u`, for the invariants `g2` and `g3`.

It satisfies the differential equation

```
WeierstrassPPrime(u, {g2, g3})^2 == 4*WeierstrassP(u, {g2, g3})^3 - g2*WeierstrassP(u, {g2, g3}) - g3
```

At `u == 0` its value is `ComplexInfinity`. For `{g2, g3} == {0, 0}` it is `-2/u^3`. For a machine number `u` and numeric invariants it evaluates numerically with machine precision, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

For the invariants `{g2, 0}` and `{0, g3}`, where the cubic `4*t^3 - g2*t - g3` factors, `FunctionExpand` rewrites it in Jacobi elliptic functions.

See
* [Wikipedia - Weierstrass's elliptic functions](https://en.wikipedia.org/wiki/Weierstrass%27s_elliptic_functions)
* [NIST - Weierstrass elliptic functions, definitions and periodic properties](https://dlmf.nist.gov/23.2)

### Examples

```
>> WeierstrassPPrime(0.5+0.25*I, {2, 3})
-1.93577+I*11.38838

>> N(WeierstrassPPrime(1/2, {2, 3}), 30)
-15.8456022596893054423588206592
```

The differential equation:

```
>> Chop(WeierstrassPPrime(0.7+0.2*I, {2, 3})^2 - (4*WeierstrassP(0.7+0.2*I, {2, 3})^3 - 2*WeierstrassP(0.7+0.2*I, {2, 3}) - 3))
0
```

The derivatives of `WeierstrassP` and `WeierstrassPPrime`:

```
>> D(WeierstrassP(z, {g2, g3}), z)
WeierstrassPPrime(z,{g2,g3})

>> D(WeierstrassPPrime(z, {g2, g3}), z)
-g2/2+6*WeierstrassP(z,{g2,g3})^2
```

Closed forms:

```
>> WeierstrassPPrime(z, {0, 0})
-2/z^3

>> WeierstrassPPrime(z, {3, 1})
-3*Sqrt(3/2)*Cot(Sqrt(3/2)*z)*Csc(Sqrt(3/2)*z)^2

>> WeierstrassPPrime(0, {2, 3})
ComplexInfinity
```

```
>> FunctionExpand(WeierstrassPPrime(z, {4, 0}))
(-4*Sqrt(2)*JacobiCN(Sqrt(2)*z,1/2)*JacobiDN(Sqrt(2)*z,1/2))/JacobiSN(Sqrt(2)*z,
1/2)^3
```

### Related terms
[JacobiSN](JacobiSN.md), [WeierstrassHalfPeriods](WeierstrassHalfPeriods.md), [WeierstrassInvariants](WeierstrassInvariants.md), [WeierstrassP](WeierstrassP.md), [WeierstrassSigma](WeierstrassSigma.md), [WeierstrassZeta](WeierstrassZeta.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of WeierstrassPPrime](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

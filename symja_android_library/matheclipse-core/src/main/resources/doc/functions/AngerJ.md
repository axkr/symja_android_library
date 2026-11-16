## AngerJ

```
AngerJ(n, z)
```

> gives the Anger function `1/Pi * Integrate(Cos(n*t - z*Sin(t)), {t, 0, Pi})`.

```
AngerJ(n, m, z)
```

> gives the associated Anger function. For `m = 0` it is the Anger function `AngerJ(n, z)`.

For an integer order `n` the Anger function is the Bessel function `BesselJ(n, z)`. `AngerJ(n, 0)` is `Sinc(n*Pi)`. `FunctionExpand` writes `AngerJ(n, z)` and `AngerJ(n, m, z)` with `HypergeometricPFQ`. Both forms evaluate numerically with an inexact number or in `N(..., n)`, also for complex orders and arguments.

See
* [Wikipedia - Anger function](https://en.wikipedia.org/wiki/Anger_function)
* [NIST - Anger and Weber functions, definitions](https://dlmf.nist.gov/11.10)

### Examples

```
>> AngerJ(2, z)
BesselJ(2,z)

>> AngerJ(n, 0)
Sinc(n*Pi)

>> AngerJ(1/2, 0)
2/Pi

>> AngerJ(1.5, 2.0)
0.403655

>> N(AngerJ(1/2, 2), 30)
0.630491092593239329346457210296

>> AngerJ(1.0+I, 2.0)
0.556505+I*(-1.29806)

>> AngerJ(0.5, 0, 2.0)
0.630491

>> D(AngerJ(n, z), z)
AngerJ(-1+n,z)/2-AngerJ(1+n,z)/2

>> Series(AngerJ(1/2, z), {z, 0, 2})
2/Pi+4/3*z/Pi-8/15*z^2/Pi+O(z)^3
```

### Related terms

[BesselJ](BesselJ.md), [HypergeometricPFQ](HypergeometricPFQ.md), [StruveH](StruveH.md), [WeberE](WeberE.md)

### Implementation status

* &#x1F9EA; - experimental

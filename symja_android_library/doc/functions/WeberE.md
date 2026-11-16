## WeberE

```
WeberE(n, z)
```

> gives the Weber function `1/Pi * Integrate(Sin(n*t - z*Sin(t)), {t, 0, Pi})`.

```
WeberE(n, m, z)
```

> gives the associated Weber function. For `m = 0` it is the Weber function `WeberE(n, z)`.

For the orders `0` and `1` the Weber function is written with the Struve function `StruveH`. `WeberE(n, 0)` is `(1-Cos(n*Pi))/(n*Pi)`, which Symja writes with `Sinc`. `FunctionExpand` writes `WeberE(n, z)` with `HypergeometricPFQ`. Both forms evaluate numerically with an inexact number or in `N(..., n)`, also for complex orders and arguments.

See
* [Wikipedia - Anger function](https://en.wikipedia.org/wiki/Anger_function)
* [NIST - Anger and Weber functions, definitions](https://dlmf.nist.gov/11.10)

### Examples

```
>> WeberE(0, z)
-StruveH(0,z)

>> WeberE(1, z)
2/Pi-StruveH(1,z)

>> WeberE(n, 0)
1/2*n*Pi*Sinc(1/2*n*Pi)^2

>> WeberE(1.5, 2.0)
0.108822

>> N(WeberE(1/2, 2), 30)
-0.406719216658737946920346628367

>> WeberE(1.0+I, 2.0)
1.21449+I*0.301319

>> WeberE(0.5, 0, 2.0)
-0.406719

>> D(WeberE(n, z), z)
WeberE(-1+n,z)/2-WeberE(1+n,z)/2
```

### Related terms

[AngerJ](AngerJ.md), [BesselJ](BesselJ.md), [HypergeometricPFQ](HypergeometricPFQ.md), [StruveH](StruveH.md)

### Implementation status

* &#x1F9EA; - experimental

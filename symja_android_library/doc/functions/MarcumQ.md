## MarcumQ

```
MarcumQ(m, a, b)
```

> gives the generalized Marcum Q-function `1/a^(m-1) * Integrate(x^m*E^(-(x^2+a^2)/2)*BesselI(m-1, a*x), {x, b, Infinity})`.

```
MarcumQ(m, a, b0, b1)
```

> gives the difference `MarcumQ(m, a, b0) - MarcumQ(m, a, b1)`.

`MarcumQ(m, a, b)` is the probability that a noncentral chi-square variable with `2*m` degrees of freedom and the noncentrality `a^2` is above `b^2`. It is even in `a`. For a real order `m > 0` and real `b >= 0` it is evaluated to arbitrary precision as the sum of `GammaRegularized(m+k, b^2/2)`, weighted with the Poisson probabilities `E^(-a^2/2)*(a^2/2)^k/k!`. All the terms are positive, so the value keeps its digits also where it is close to `0` or `1`. `MarcumQ(m, a, b0, b1)` is calculated with as many extra digits as cancel in the difference.

See
* [Wikipedia - Marcum Q-function](https://en.wikipedia.org/wiki/Marcum_Q-function)

### Examples

```
>> MarcumQ(1, 2.0, 3.0)
0.214362

>> N(MarcumQ(5/2, 3, 1), 30)
0.999253075609070362524576164408

>> MarcumQ({1, 2, 3}, 2.0, 3.0)
{0.214362,0.352698,0.510966}

>> MarcumQ(m, -a, b)
MarcumQ(m,a,b)

>> MarcumQ(m, 0, b)
GammaRegularized(m,b^2/2)

>> MarcumQ(2, a, 0)
1

>> MarcumQ(m, a, Infinity)
0

>> MarcumQ(m, a, b0, Infinity)
MarcumQ(m,a,b0)

>> N(MarcumQ(1, 1/2, 0, 1/1000000), 30)
0.000000000000441248451292201178333725895425
```

The cumulative distribution functions of the `RiceDistribution` and the `NoncentralChiSquareDistribution` are defined with `MarcumQ`:

```
>> CDF(RiceDistribution(1, 2), x)
Piecewise({{MarcumQ(1,1/2,0,x/2),x>0}},0)

>> CDF(RiceDistribution(1, 2), 3.0)
0.630931

>> CDF(NoncentralChiSquareDistribution(2, 3), 4.0)
0.493562
```

### Related terms

[BesselI](BesselI.md), [CDF](CDF.md), [GammaRegularized](GammaRegularized.md), [OwenT](OwenT.md)

### Implementation status

* &#x2611; - partially implemented

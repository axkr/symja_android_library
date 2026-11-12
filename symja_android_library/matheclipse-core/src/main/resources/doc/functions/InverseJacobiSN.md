## InverseJacobiSN

```
InverseJacobiSN(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiSN](JacobiSN.md): the `u` with `JacobiSN(u, m) == z`.

For `m == 0` it is `ArcSin(z)`, and for `m == 1` it is `ArcTanh(z)`. It is an odd function of `z`, and `InverseJacobiSN(1, m)` is the quarter period `EllipticK(m)`. For `-1 <= z <= 1` and `m < 1` it is the incomplete elliptic integral `EllipticF(ArcSin(z), m)`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiSN(0.5, 0.3)
0.530637

>> JacobiSN(InverseJacobiSN(0.5, 0.3), 0.3)
0.5
```

```
>> InverseJacobiSN(1/2, 1/3)
InverseJacobiSN(1/2,1/3)

>> N(InverseJacobiSN(1/2, 1/3), 30)
0.531450669512051629226186228291
```

Outside of `-1 <= z <= 1` the value is complex, and a list of arguments gives a list:

```
>> InverseJacobiSN(2.0, 0.3)
1.21719+I*2.07536

>> InverseJacobiSN({0.1, 0.2, 0.3}, 0.5)
{0.100251,0.202039,0.307055}
```

For `m == 1/2` the quarter period has a closed form:

```
>> InverseJacobiSN(1, 1/2)
(8*Pi^(3/2))/Gamma(-1/4)^2

>> N(InverseJacobiSN(1, 1/2))
1.85407
```

For `m -> Infinity` the function `JacobiSN(u, m)` oscillates ever faster, and the inverse tends to `0`:

```
>> InverseJacobiSN(z, Infinity)
0
```

The special values:

```
>> {InverseJacobiSN(z, 0), InverseJacobiSN(z, 1), InverseJacobiSN(1, m), InverseJacobiSN(-1, m), InverseJacobiSN(0, m), InverseJacobiSN(-z, m)}
{ArcSin(z),ArcTanh(z),EllipticK(m),-EllipticK(m),0,-InverseJacobiSN(z,m)}
```

### Related terms
[EllipticF](EllipticF.md), [EllipticK](EllipticK.md), [InverseJacobiNS](InverseJacobiNS.md), [JacobiSN](JacobiSN.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiSN](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

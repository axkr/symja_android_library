## InverseJacobiCS

```
InverseJacobiCS(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiCS](JacobiCS.md): the `u` with `JacobiCS(u, m) == z`.

The inverse of a reciprocal function at `z` is the inverse of the function itself at `1/z`: `InverseJacobiCS(z, m) == InverseJacobiSC(1/z, m)`, as `ArcCsc(z) == ArcSin(1/z)`.

For `m == 0` it is `ArcCot(z)`, and for `m == 1` it is `ArcCsch(z)`. It is an odd function of `z`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiCS(2.5, 0.3)
0.383234

>> JacobiCS(InverseJacobiCS(2.5, 0.3), 0.3)
2.5
```

```
>> N(InverseJacobiCS(3, 1/3), 30)
0.323591010240834290656564651301
```

A complex argument:

```
>> InverseJacobiCS(0.5+I, 0.3)
0.628461+I*(-0.73996)
```

The special values:

```
>> {InverseJacobiCS(z, 0), InverseJacobiCS(z, 1), InverseJacobiCS(-z, m)}
{ArcCot(z),ArcCsch(z),-InverseJacobiCS(z,m)}
```

### Related terms
[EllipticK](EllipticK.md), [InverseJacobiSC](InverseJacobiSC.md), [JacobiCS](JacobiCS.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiCS](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

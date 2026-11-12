## InverseJacobiNS

```
InverseJacobiNS(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiNS](JacobiNS.md): the `u` with `JacobiNS(u, m) == z`.

The inverse of a reciprocal function at `z` is the inverse of the function itself at `1/z`: `InverseJacobiNS(z, m) == InverseJacobiSN(1/z, m)`, as `ArcCsc(z) == ArcSin(1/z)`.

For `m == 0` it is `ArcCsc(z)`, and for `m == 1` it is `ArcCoth(z)`. It is an odd function of `z`, and `InverseJacobiNS(1, m)` is the quarter period `EllipticK(m)`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiNS(2.5, 0.3)
0.414961

>> JacobiNS(InverseJacobiNS(2.5, 0.3), 0.3)
2.5
```

```
>> N(InverseJacobiNS(3, 1/3), 30)
0.34200407342843274031611277371
```

The special values:

```
>> {InverseJacobiNS(z, 0), InverseJacobiNS(z, 1), InverseJacobiNS(1, m), InverseJacobiNS(-1, m)}
{ArcCsc(z),ArcCoth(z),EllipticK(m),-EllipticK(m)}
```

### Related terms
[EllipticK](EllipticK.md), [InverseJacobiSN](InverseJacobiSN.md), [JacobiNS](JacobiNS.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiNS](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

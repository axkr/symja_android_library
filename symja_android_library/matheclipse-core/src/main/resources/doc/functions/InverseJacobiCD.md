## InverseJacobiCD

```
InverseJacobiCD(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiCD](JacobiCD.md): the `u` with `JacobiCD(u, m) == z`.

For `m == 0` it is `ArcCos(z)`. At `z == 0` it is the quarter period `EllipticK(m)`, and at `z == 1` it is `0`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiCD(0.5, 0.3)
1.18325

>> JacobiCD(InverseJacobiCD(0.5, 0.3), 0.3)
0.5
```

```
>> N(InverseJacobiCD(1/2, 1/3), 30)
1.20246621574588339585873609738
```

The special values:

```
>> {InverseJacobiCD(z, 0), InverseJacobiCD(0, m), InverseJacobiCD(1, m)}
{ArcCos(z),EllipticK(m),0}
```

### Related terms
[EllipticK](EllipticK.md), [InverseJacobiDC](InverseJacobiDC.md), [JacobiCD](JacobiCD.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiCD](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

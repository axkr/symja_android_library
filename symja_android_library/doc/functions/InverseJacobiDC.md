## InverseJacobiDC

```
InverseJacobiDC(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiDC](JacobiDC.md): the `u` with `JacobiDC(u, m) == z`.

The inverse of a reciprocal function at `z` is the inverse of the function itself at `1/z`: `InverseJacobiDC(z, m) == InverseJacobiCD(1/z, m)`, as `ArcCsc(z) == ArcSin(1/z)`.

For `m == 0` it is `ArcSec(z)`. For `m == 1` the function `JacobiDC(u, 1) == 1` has no inverse, and the expression stays unevaluated. At `z == 1` the value is `0`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiDC(2.5, 0.3)
1.29893

>> JacobiDC(InverseJacobiDC(2.5, 0.3), 0.3)
2.5
```

```
>> N(InverseJacobiDC(3, 1/3), 30)
1.39191281182950228476880955196
```

The special values:

```
>> {InverseJacobiDC(z, 0), InverseJacobiDC(z, 1), InverseJacobiDC(1, m)}
{ArcSec(z),InverseJacobiDC(z,1),0}
```

### Related terms
[EllipticK](EllipticK.md), [InverseJacobiCD](InverseJacobiCD.md), [JacobiDC](JacobiDC.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiDC](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

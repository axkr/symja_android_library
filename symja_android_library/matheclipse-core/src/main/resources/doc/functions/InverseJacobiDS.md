## InverseJacobiDS

```
InverseJacobiDS(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiDS](JacobiDS.md): the `u` with `JacobiDS(u, m) == z`.

The inverse of a reciprocal function at `z` is the inverse of the function itself at `1/z`: `InverseJacobiDS(z, m) == InverseJacobiSD(1/z, m)`, as `ArcCsc(z) == ArcSin(1/z)`.

For `m == 0` it is `ArcCsc(z)`, and for `m == 1` it is `ArcCsch(z)`. It is an odd function of `z`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiDS(2.5, 0.3)
0.404626

>> JacobiDS(InverseJacobiDS(2.5, 0.3), 0.3)
2.5
```

```
>> N(InverseJacobiDS(3, 1/3), 30)
0.3355213426582686512533670275
```

The special values:

```
>> {InverseJacobiDS(z, 0), InverseJacobiDS(z, 1), InverseJacobiDS(-z, m)}
{ArcCsc(z),ArcCsch(z),-InverseJacobiDS(z,m)}
```

### Related terms
[EllipticK](EllipticK.md), [InverseJacobiSD](InverseJacobiSD.md), [JacobiDS](JacobiDS.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiDS](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

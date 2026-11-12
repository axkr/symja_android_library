## InverseJacobiSD

```
InverseJacobiSD(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiSD](JacobiSD.md): the `u` with `JacobiSD(u, m) == z`.

For `m == 0` it is `ArcSin(z)`, and for `m == 1` it is `ArcSinh(z)`. It is an odd function of `z`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiSD(0.5, 0.3)
0.509471

>> JacobiSD(InverseJacobiSD(0.5, 0.3), 0.3)
0.5
```

```
>> N(InverseJacobiSD(1/2, 1/3), 30)
0.507985306007726029062028998933
```

The special values:

```
>> {InverseJacobiSD(z, 0), InverseJacobiSD(z, 1), InverseJacobiSD(0, m), InverseJacobiSD(-z, m)}
{ArcSin(z),ArcSinh(z),0,-InverseJacobiSD(z,m)}
```

### Related terms
[EllipticK](EllipticK.md), [InverseJacobiDS](InverseJacobiDS.md), [JacobiSD](JacobiSD.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiSD](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

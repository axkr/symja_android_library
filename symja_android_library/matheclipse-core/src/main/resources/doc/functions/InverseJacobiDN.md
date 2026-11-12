## InverseJacobiDN

```
InverseJacobiDN(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiDN](JacobiDN.md): the `u` with `JacobiDN(u, m) == z`.

For `m == 1` it is `ArcSech(z)`. At `z == 1` the value is `0`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiDN(0.9, 0.3)
0.956626

>> JacobiDN(InverseJacobiDN(0.9, 0.3), 0.3)
0.9
```

```
>> N(InverseJacobiDN(9/10, 1/3), 30)
0.888730622494084014267458239133
```

The special values:

```
>> {InverseJacobiDN(z, 1), InverseJacobiDN(1, m)}
{ArcSech(z),0}
```

### Related terms
[EllipticK](EllipticK.md), [InverseJacobiND](InverseJacobiND.md), [JacobiDN](JacobiDN.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiDN](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

## InverseJacobiND

```
InverseJacobiND(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiND](JacobiND.md): the `u` with `JacobiND(u, m) == z`.

The inverse of a reciprocal function at `z` is the inverse of the function itself at `1/z`: `InverseJacobiND(z, m) == InverseJacobiDN(1/z, m)`, as `ArcCsc(z) == ArcSin(1/z)`.

For `m == 1` it is `ArcCosh(z)`. For `m == 0` the function `JacobiND(u, 0) == 1` has no inverse, and the expression stays unevaluated. At `z == 1` the value is `0`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiND(1.1, 0.3)
0.894574

>> JacobiND(InverseJacobiND(1.1, 0.3), 0.3)
1.1
```

```
>> N(InverseJacobiND(11/10, 1/3), 30)
0.833936205081374594911727396722
```

The special values:

```
>> {InverseJacobiND(z, 0), InverseJacobiND(z, 1), InverseJacobiND(1, m)}
{InverseJacobiND(z,0),ArcCosh(z),0}
```

### Related terms
[EllipticK](EllipticK.md), [InverseJacobiDN](InverseJacobiDN.md), [JacobiND](JacobiND.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiND](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

## InverseJacobiCN

```
InverseJacobiCN(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiCN](JacobiCN.md): the `u` with `JacobiCN(u, m) == z`.

For `m == 0` it is `ArcCos(z)`, and for `m == 1` it is `ArcSech(z)`. At `z == 1, 0, -1` the values are `0`, `EllipticK(m)` and `2*EllipticK(m)`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiCN(0.5, 0.3)
1.09914

>> JacobiCN(InverseJacobiCN(0.5, 0.3), 0.3)
0.5
```

```
>> N(InverseJacobiCN(1/2, 1/3), 30)
1.10577196081148841593683352229
```

A complex argument:

```
>> InverseJacobiCN(0.5+I, 0.3)
1.18935+I*(-1.12135)
```

The special values:

```
>> {InverseJacobiCN(z, 0), InverseJacobiCN(z, 1), InverseJacobiCN(-1, m), InverseJacobiCN(0, m), InverseJacobiCN(1, m)}
{ArcCos(z),ArcSech(z),2*EllipticK(m),EllipticK(m),0}
```

### Related terms
[EllipticK](EllipticK.md), [InverseJacobiNC](InverseJacobiNC.md), [JacobiCN](JacobiCN.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiCN](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

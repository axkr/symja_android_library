## InverseJacobiSC

```
InverseJacobiSC(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiSC](JacobiSC.md): the `u` with `JacobiSC(u, m) == z`.

For `m == 0` it is `ArcTan(z)`, and for `m == 1` it is `ArcSinh(z)`. It is an odd function of `z`, and `InverseJacobiSC(I, m)` is `I*EllipticK(1-m)`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiSC(0.5, 0.3)
0.468557

>> JacobiSC(InverseJacobiSC(0.5, 0.3), 0.3)
0.5
```

```
>> N(InverseJacobiSC(1/2, 1/3), 30)
0.469119487327158746673308497325
```

The special values:

```
>> {InverseJacobiSC(z, 0), InverseJacobiSC(z, 1), InverseJacobiSC(0, m), InverseJacobiSC(I, m), InverseJacobiSC(-z, m)}
{ArcTan(z),ArcSinh(z),0,I*EllipticK(1-m),-InverseJacobiSC(z,m)}
```

### Related terms
[EllipticK](EllipticK.md), [InverseJacobiCS](InverseJacobiCS.md), [JacobiSC](JacobiSC.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiSC](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

## InverseJacobiNC

```
InverseJacobiNC(z, m)
```

> returns the inverse of the Jacobi elliptic function [JacobiNC](JacobiNC.md): the `u` with `JacobiNC(u, m) == z`.

The inverse of a reciprocal function at `z` is the inverse of the function itself at `1/z`: `InverseJacobiNC(z, m) == InverseJacobiCN(1/z, m)`, as `ArcCsc(z) == ArcSin(1/z)`.

For `m == 0` it is `ArcSec(z)`, and for `m == 1` it is `ArcCosh(z)`. At `z == 1` the value is `0`. Apart from these special values, it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Inverse Jacobian elliptic functions](https://dlmf.nist.gov/22.15)

### Examples

```
>> InverseJacobiNC(2.5, 0.3)
1.22761

>> JacobiNC(InverseJacobiNC(2.5, 0.3), 0.3)
2.5
```

```
>> N(InverseJacobiNC(3, 1/3), 30)
1.3215221757527599880130607017
```

For `m -> Infinity` the function `JacobiCN(u, m)` oscillates ever faster, and the inverse tends to `0`:

```
>> InverseJacobiNC(z, Infinity)
0
```

The special values:

```
>> {InverseJacobiNC(z, 0), InverseJacobiNC(z, 1), InverseJacobiNC(1, m)}
{ArcSec(z),ArcCosh(z),0}
```

### Related terms
[EllipticK](EllipticK.md), [InverseJacobiCN](InverseJacobiCN.md), [JacobiNC](JacobiNC.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseJacobiNC](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

## JacobiNC

```
JacobiNC(u, m)
```

> returns the Jacobi elliptic function `nc(u, m)`, which is `1/JacobiCN(u, m)`.

`JacobiNC` is the reciprocal of [JacobiCN](JacobiCN.md). As for the other Jacobi elliptic functions, `m` is the parameter, the square of the elliptic modulus `k`.

For `m == 0` the function is `Sec(u)`, for `m == 1` it is `Cosh(u)`, and at `u == 0` its value is `1`. Otherwise it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Jacobian elliptic functions, definitions](https://dlmf.nist.gov/22.2)

### Examples

```
>> JacobiNC(1.5, 0.3)
5.60539

>> 1/JacobiCN(1.5, 0.3)
5.60539
```

```
>> N(JacobiNC(2, 1/3), 30)
-4.62153404848079684747412916549
```

The special values for `m == 0`, `m == 1` and `u == 0`:

```
>> {JacobiNC(x, 0), JacobiNC(x, 1), JacobiNC(0, m)}
{Sec(x),Cosh(x),1}
```

### Related terms
[InverseJacobiNC](InverseJacobiNC.md), [JacobiCD](JacobiCD.md), [JacobiCN](JacobiCN.md), [JacobiDN](JacobiDN.md), [JacobiSC](JacobiSC.md), [JacobiSD](JacobiSD.md), [JacobiSN](JacobiSN.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of JacobiNC](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

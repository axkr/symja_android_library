## JacobiDC

```
JacobiDC(u, m)
```

> returns the Jacobi elliptic function `dc(u, m)`, which is `JacobiDN(u, m)/JacobiCN(u, m)`.

`JacobiDC` is the reciprocal of [JacobiCD](JacobiCD.md). As for the other Jacobi elliptic functions, `m` is the parameter, the square of the elliptic modulus `k`.

For `m == 0` the function is `Sec(u)`, for `m == 1` it is `1`, and at `u == 0` its value is `1`. Otherwise it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Jacobian elliptic functions, definitions](https://dlmf.nist.gov/22.2)

### Examples

```
>> JacobiDC(1.5, 0.3)
4.72168

>> 1/JacobiCD(1.5, 0.3)
4.72168
```

```
>> N(JacobiDC(2, 1/3), 30)
-3.81737928962329724865282377526
```

The special values for `m == 0`, `m == 1` and `u == 0`:

```
>> {JacobiDC(x, 0), JacobiDC(x, 1), JacobiDC(0, m)}
{Sec(x),1,1}
```

### Related terms
[InverseJacobiDC](InverseJacobiDC.md), [JacobiCD](JacobiCD.md), [JacobiCN](JacobiCN.md), [JacobiDN](JacobiDN.md), [JacobiSC](JacobiSC.md), [JacobiSD](JacobiSD.md), [JacobiSN](JacobiSN.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of JacobiDC](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

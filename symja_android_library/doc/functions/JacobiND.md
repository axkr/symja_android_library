## JacobiND

```
JacobiND(u, m)
```

> returns the Jacobi elliptic function `nd(u, m)`, which is `1/JacobiDN(u, m)`.

`JacobiND` is the reciprocal of [JacobiDN](JacobiDN.md). As for the other Jacobi elliptic functions, `m` is the parameter, the square of the elliptic modulus `k`.

For `m == 0` the function is `1`, for `m == 1` it is `Cosh(u)`, and at `u == 0` its value is `1`. Otherwise it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Jacobian elliptic functions, definitions](https://dlmf.nist.gov/22.2)

### Examples

```
>> JacobiND(1.5, 0.3)
1.18716

>> 1/JacobiDN(1.5, 0.3)
1.18716
```

```
>> N(JacobiND(2, 1/3), 30)
1.21065623765587474100188719622
```

The special values for `m == 0`, `m == 1` and `u == 0`:

```
>> {JacobiND(x, 0), JacobiND(x, 1), JacobiND(0, m)}
{1,Cosh(x),1}
```

### Related terms
[InverseJacobiND](InverseJacobiND.md), [JacobiCD](JacobiCD.md), [JacobiCN](JacobiCN.md), [JacobiDN](JacobiDN.md), [JacobiSC](JacobiSC.md), [JacobiSD](JacobiSD.md), [JacobiSN](JacobiSN.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of JacobiND](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

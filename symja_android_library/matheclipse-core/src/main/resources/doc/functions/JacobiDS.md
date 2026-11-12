## JacobiDS

```
JacobiDS(u, m)
```

> returns the Jacobi elliptic function `ds(u, m)`, which is `JacobiDN(u, m)/JacobiSN(u, m)`.

`JacobiDS` is the reciprocal of [JacobiSD](JacobiSD.md). As for the other Jacobi elliptic functions, `m` is the parameter, the square of the elliptic modulus `k`.

For `m == 0` the function is `Csc(u)`, for `m == 1` it is `Csch(u)`, and at `u == 0` its value is `ComplexInfinity`. Otherwise it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Jacobian elliptic functions, definitions](https://dlmf.nist.gov/22.2)

### Examples

```
>> JacobiDS(1.5, 0.3)
0.85608

>> 1/JacobiSD(1.5, 0.3)
0.85608
```

```
>> N(JacobiDS(2, 1/3), 30)
0.846041378044112145910310223666
```

The special values for `m == 0`, `m == 1` and `u == 0`:

```
>> {JacobiDS(x, 0), JacobiDS(x, 1), JacobiDS(0, m)}
{Csc(x),Csch(x),ComplexInfinity}
```

### Related terms
[InverseJacobiDS](InverseJacobiDS.md), [JacobiCD](JacobiCD.md), [JacobiCN](JacobiCN.md), [JacobiDN](JacobiDN.md), [JacobiSC](JacobiSC.md), [JacobiSD](JacobiSD.md), [JacobiSN](JacobiSN.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of JacobiDS](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

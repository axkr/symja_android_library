## JacobiNS

```
JacobiNS(u, m)
```

> returns the Jacobi elliptic function `ns(u, m)`, which is `1/JacobiSN(u, m)`.

`JacobiNS` is the reciprocal of [JacobiSN](JacobiSN.md). As for the other Jacobi elliptic functions, `m` is the parameter, the square of the elliptic modulus `k`.

For `m == 0` the function is `Csc(u)`, for `m == 1` it is `Coth(u)`, and at `u == 0` its value is `ComplexInfinity`. Otherwise it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Jacobian elliptic functions, definitions](https://dlmf.nist.gov/22.2)

### Examples

```
>> JacobiNS(1.5, 0.3)
1.0163

>> 1/JacobiSN(1.5, 0.3)
1.0163
```

```
>> N(JacobiNS(2, 1/3), 30)
1.02426527164407640027533290125
```

A complex argument, and the derivative:

```
>> JacobiNS(2.0+0.5*I, 0.7)
0.966132+I*(-0.00947417)

>> D(JacobiNS(x, m), x)
-JacobiCS(x,m)*JacobiDS(x,m)
```

The special values for `m == 0`, `m == 1` and `u == 0`:

```
>> {JacobiNS(x, 0), JacobiNS(x, 1), JacobiNS(0, m)}
{Csc(x),Coth(x),ComplexInfinity}
```

### Related terms
[InverseJacobiNS](InverseJacobiNS.md), [JacobiCD](JacobiCD.md), [JacobiCN](JacobiCN.md), [JacobiDN](JacobiDN.md), [JacobiSC](JacobiSC.md), [JacobiSD](JacobiSD.md), [JacobiSN](JacobiSN.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of JacobiNS](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

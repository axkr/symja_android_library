## JacobiCS

```
JacobiCS(u, m)
```

> returns the Jacobi elliptic function `cs(u, m)`, which is `JacobiCN(u, m)/JacobiSN(u, m)`.

`JacobiCS` is the reciprocal of [JacobiSC](JacobiSC.md). As for the other Jacobi elliptic functions, `m` is the parameter, the square of the elliptic modulus `k`.

For `m == 0` the function is `Cot(u)`, for `m == 1` it is `Csch(u)`, and at `u == 0` its value is `ComplexInfinity`. Otherwise it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact numbers it stays unevaluated.

See
* [Wikipedia - Jacobi elliptic functions](https://en.wikipedia.org/wiki/Jacobi_elliptic_functions)
* [NIST - Jacobian elliptic functions, definitions](https://dlmf.nist.gov/22.2)

### Examples

```
>> JacobiCS(1.5, 0.3)
0.181308

>> 1/JacobiSC(1.5, 0.3)
0.181308
```

```
>> JacobiCS(2, 1/3)
JacobiCS(2,1/3)

>> N(JacobiCS(2, 1/3), 30)
-0.221628848970781822179244735668
```

A complex argument, and a list of arguments:

```
>> JacobiCS(0.5+I, 0.25)
0.273347+I*(-1.08609)

>> JacobiCS({0.5, 1.0, 1.5}, 0.5)
{1.87417,0.742186,0.258497}
```

The special values for `m == 0`, `m == 1` and `u == 0`:

```
>> {JacobiCS(x, 0), JacobiCS(x, 1), JacobiCS(0, m)}
{Cot(x),Csch(x),ComplexInfinity}
```

### Related terms
[InverseJacobiCS](InverseJacobiCS.md), [JacobiCD](JacobiCD.md), [JacobiCN](JacobiCN.md), [JacobiDN](JacobiDN.md), [JacobiSC](JacobiSC.md), [JacobiSD](JacobiSD.md), [JacobiSN](JacobiSN.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of JacobiCS](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

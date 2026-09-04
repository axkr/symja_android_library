## Casoratian

```
Casoratian({y1, y2, ...}, n)
```

> returns the Casoratian determinant of the sequences `y1, y2, ...` of the variable `n`.

```
Casoratian(equation, y, n)
```

> returns the Casoratian determinant of a basis of the solutions of the linear difference `equation` with dependent variable `y` and independent variable `n`.

```
Casoratian(equations, {y1, y2, ...}, n)
```

> returns the Casoratian determinant of a system of linear difference `equations` of first order.

The Casoratian is to a difference equation what the [Wronskian](Wronskian.md) is to a differential
one. For `m` sequences it is the determinant of the matrix whose rows are the sequences shifted by
`0` to `m-1`, and it vanishes identically when they are linearly dependent.

See:  
* [Wikipedia - Casoratian](https://en.wikipedia.org/wiki/Casoratian)
* [Wikipedia - Abel's identity](https://en.wikipedia.org/wiki/Abel%27s_identity)

### Examples

```
>> Casoratian({2^n, n*2^n}, n)
2^(1+2*n)

>> Casoratian({1, n}, n)
1

>> Casoratian({1, n, n^2}, n)
2
```

A Casoratian which vanishes identically means the sequences are linearly dependent.

```
>> Casoratian({2^n, 2^(n+1)}, n)
0

>> Casoratian({1, n, 2*n + 3}, n)
0

>> Casoratian({f(n), c*f(n)}, n)
0
```

For undetermined sequences the determinant is returned as it stands.

```
>> Casoratian({f(n), g(n)}, n)
-f(1+n)*g(n)+f(n)*g(1+n)
```

For a linear difference equation the solutions do not have to be known. The Casoratian of a basis
satisfies the recurrence `C(n+1) == (-1)^m*a(0)/a(m)*C(n)`, so the lowest and the highest
coefficient determine it. The recurrence fixes it only up to a constant factor, and the one
returned here is normalized to `C(0) == 1`.

```
>> Casoratian(y(n+2) - 3*y(n+1) + 2*y(n) == 0, y, n)
2^n

>> Casoratian(y(n+2) - y(n+1) - y(n) == 0, y, n)
(-1)^n

>> Casoratian(y(n+2) + a*y(n+1) + b*y(n) == 0, y, n)
b^n
```

A coefficient which depends on `n` leaves a product, which is evaluated when it has a closed form.

```
>> Casoratian(y(n+2) - (n+1)*y(n) == 0, y, n)
(-1)^n*n!
```

For a system of first order the Casoratian accumulates the determinant of its coefficient matrix.

```
>> Casoratian({y(n+1) == 2*y(n) + z(n), z(n+1) == y(n) + 2*z(n)}, {y, z}, n)
3^n
```

An equation which is not linear in the dependent variable has no basis of solutions, and is
returned unevaluated.

```
>> Casoratian(y(n)^2 == 0, y, n)
Casoratian(y(n)^2==0,y,n)
```

### Related terms
[Det](Det.md), [RSolve](RSolve.md), [Wronskian](Wronskian.md)

### Implementation status

* &#x2705; - full supported

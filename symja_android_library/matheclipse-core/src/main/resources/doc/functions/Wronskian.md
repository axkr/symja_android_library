## Wronskian

```
Wronskian({y1, y2, ...}, x)
```

> returns the Wronskian determinant of the functions `y1, y2, ...` of the variable `x`.

```
Wronskian(equation, y, x)
```

> returns the Wronskian determinant of a basis of the solutions of the linear differential `equation` with dependent variable `y` and independent variable `x`.

```
Wronskian(equations, {y1, y2, ...}, x)
```

> returns the Wronskian determinant of a system of linear differential `equations` of first order.

The Wronskian of `m` functions is the determinant of the matrix whose rows are the functions and
their derivatives up to order `m-1`. It vanishes everywhere when the functions are linearly
dependent, which makes it the usual test for a set of solutions of a linear differential equation
being a basis of them.

See:  
* [Wikipedia - Wronskian](https://en.wikipedia.org/wiki/Wronskian)
* [Wikipedia - Abel's identity](https://en.wikipedia.org/wiki/Abel%27s_identity)

### Examples

```
>> Wronskian({Exp(x), Exp(2*x)}, x)
E^(3*x)

>> Wronskian({Cos(x), Sin(x)}, x)
1

>> Wronskian({x, x^2}, x)
x^2

>> Wronskian({1/x, 1/(x + 1)}, x)
1/(x^2*(1+x)^2)
```

A Wronskian which vanishes everywhere means the functions are linearly dependent.

```
>> Wronskian({Exp(x), Exp(x + 3)}, x)
0

>> Wronskian({x^2, 3*x^2 + 5*x + 1, x^2 + 5*x + 1}, x)
0

>> Wronskian({f(x), c*f(x)}, x)
0
```

For undetermined functions the determinant is returned as it stands.

```
>> Wronskian({f(x), g(x)}, x)
-g(x)*f'(x)+f(x)*g'(x)
```

For a linear differential equation the solutions do not have to be known. Abel's identity gives
the Wronskian of a basis from the coefficient of the second highest derivative alone, so an answer
comes even for equations no solver can solve. It is determined only up to a constant factor, and
the one returned here carries no arbitrary constant.

```
>> Wronskian(y''(x) - x*y(x) == 0, y, x)
1

>> Wronskian(y'''(x) - 5*y''(x) + 11*y(x) == 0, y, x)
E^(5*x)

>> Wronskian(y''(x) - x*y'(x) + y(x) == 0, y, x)
E^(x^2/2)

>> Wronskian(y''(x) + a*y'(x) + b*y(x) == 0, y, x)
E^(-a*x)

>> Wronskian(y''(x) + y'(x) + BesselJ(1, x)*y(x) == 0, y, x)
E^(-x)
```

For a system of first order the Wronskian follows from the trace of its coefficient matrix.

```
>> Wronskian({y'(x) == y(x) - z(x), z'(x) == y(x) + z(x)}, {y, z}, x)
E^(2*x)

>> Wronskian({y'(x) == v(x), v'(x) == z(x), z'(x) == y(x) - z(x)}, {v, y, z}, x)
E^(-x)
```

An equation which is not linear in the dependent variable has no basis of solutions, and is
returned unevaluated.

```
>> Wronskian(y'(x)^2 == 0, y, x)
Wronskian(y'(x)^2==0,y,x)
```

### Related terms
[D](D.md), [Det](Det.md), [DSolve](DSolve.md), [DSolveValue](DSolveValue.md)

### Implementation status

* &#x2705; - full supported

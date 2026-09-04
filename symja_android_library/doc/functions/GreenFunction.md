## GreenFunction

```
GreenFunction({operator, condition1, condition2}, y, {x, xmin, xmax}, s)
```

> returns the Green's function of the linear differential `operator` in `y(x)` on the interval from `xmin` to `xmax`, for the boundary conditions given, with the source at `s`.

The Green's function `G(x,s)` solves the equation with a unit impulse at `s` as its right hand
side. The solution of the equation with any right hand side `f` and the same boundary conditions is
then `Integrate(G(x,s)*f(s), {s, xmin, xmax})`.

It is built from the solutions of the homogeneous equation which meet one boundary condition each,
so a problem whose homogeneous equation already has a solution meeting both conditions has no
Green's function and is returned unevaluated.

Second order operators with two separated boundary conditions are supported.

See:  
* [Wikipedia - Green's function](https://en.wikipedia.org/wiki/Green%27s_function)

### Examples

```
>> GreenFunction({y''(x), y(0) == 0, y(1) == 0}, y, {x, 0, 1}, s)
Piecewise({{(-1+s)*x,x<=s}},s*(-1+x))

>> GreenFunction({y''(x) + y(x), y(0) == 0, y(Pi/2) == 0}, y, {x, 0, Pi/2}, s)
Piecewise({{-Cos(s)*Sin(x),x<=s}},-Cos(x)*Sin(s))
```

A condition on the derivative is allowed as well.

```
>> GreenFunction({y''(x), y(0) == 0, y'(1) == 0}, y, {x, 0, 1}, s)
Piecewise({{-x,x<=s}},-s)
```

The coefficients need not be constant.

```
>> GreenFunction({x^2*y''(x) + x*y'(x), y(1) == 0, y(E) == 0}, y, {x, 1, E}, s)
Piecewise({{((-1+Log(s))*Log(x))/s,x<=s}},(Log(s)*(-1+Log(x)))/s)
```

Integrating the Green's function against a right hand side solves the boundary value problem.

```
>> Simplify(Integrate(x*(s-1), {s, x, 1}) + Integrate(s*(x-1), {s, 0, x}))
1/2*(-1+x)*x
```

`Sin(x)` solves the next equation and meets both of its conditions, so that problem has no Green's
function.

```
>> GreenFunction({y''(x) + y(x), y(0) == 0, y(Pi) == 0}, y, {x, 0, Pi}, s)
GreenFunction({y(x)+y''(x),y(0)==0,y(Pi)==0},y,{x,0,Pi},s)
```

### Related terms
[DSolve](DSolve.md), [DiracDelta](DiracDelta.md), [Wronskian](Wronskian.md)

### Implementation status

* &#x2611; - partially implemented

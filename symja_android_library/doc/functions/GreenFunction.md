## GreenFunction

```
GreenFunction({operator, conditions}, y, {x, xmin, xmax}, s)
```

> returns the Green's function of the linear differential `operator` in `y(x)` on the interval from `xmin` to `xmax`, for the homogeneous boundary conditions given, with the source at `s`.

The Green's function `G(x,s)` is the response to a unit impulse at `s`: it solves the equation with
`DiracDelta(x-s)` as its right hand side and meets the conditions. The solution of the equation with
any right hand side `f` and the same conditions is then
`Integrate(G(x,s)*f(s), {s, xmin, xmax})`.

Asking about `y` rather than `y(x)` gives the function itself instead of an expression.

Operators of first and second order are supported, with the conditions either at both ends of the
interval, which is a boundary value problem, or all at the near end, which is an initial value
problem and gives a causal Green's function.

See:  
* [Wikipedia - Green's function](https://en.wikipedia.org/wiki/Green%27s_function)

### Examples

```
>> GreenFunction({-u''(x), u(0) == 0, u(1) == 0}, u(x), {x, 0, 1}, y)
(y-x*y)*HeavisideTheta(x-y)+(x-x*y)*HeavisideTheta(-x+y)

>> GreenFunction({u''(x) + u(x), u(0) == 0, u(Pi/2) == 0}, u(x), {x, 0, Pi/2}, s)
-Cos(x)*HeavisideTheta(-s+x)*Sin(s)-Cos(s)*HeavisideTheta(s-x)*Sin(x)
```

A condition may prescribe the derivative, or combine it with the value.

```
>> GreenFunction({u''(x) + u(x), u'(0) == 0, u'(Pi/2) == 0}, u(x), {x, 0, Pi/2}, s)
Cos(x)*HeavisideTheta(s-x)*Sin(s)+Cos(s)*HeavisideTheta(-s+x)*Sin(x)
```

The coefficients and the endpoints may be symbolic.

```
>> GreenFunction({x^2*u''(x) + x*u'(x), u(1) == 0, u(E) == 0}, u(x), {x, 1, E}, s)
(HeavisideTheta(-s+x)*Log(s)*(-1+Log(x)))/s+(HeavisideTheta(s-x)*(-1+Log(s))*Log(x))/s

>> GreenFunction({T*u''(x), u(0) == 0, u(p) == 0}, u(x), {x, 0, p}, y)
((-p+x)*y*HeavisideTheta(x-y))/(p*T)+(x*(-p+y)*HeavisideTheta(-x+y))/(p*T)
```

With all the conditions at the near end the Green's function is causal, and is the response of the
system to an impulse.

```
>> GreenFunction({u''(x) + 5*u'(x) + 6*u(x), u(0) == 0, u'(0) == 0}, u(x), {x, 0, Infinity}, y)
(E^(2*(-x+y))-E^(3*(-x+y)))*HeavisideTheta(x-y)

>> GreenFunction({u'(x) + u(x), u(0) == 0}, u(x), {x, 0, Infinity}, y)
HeavisideTheta(x-y)/E^(x-y)
```

Asking about `u` gives the function itself.

```
>> GreenFunction({u''(x) + 5*u'(x) + 6*u(x), u(0) == 0, u'(0) == 0}, u, {x, 0, Infinity}, y)
Function({x,y},(E^(2*(-x+y))-E^(3*(-x+y)))*HeavisideTheta(x-y))
```

Integrating the Green's function against a right hand side solves the boundary value problem.

```
>> Simplify(Integrate(x*(s-1), {s, x, 1}) + Integrate(s*(x-1), {s, 0, x}))
1/2*(-1+x)*x
```

`Sin(x)` solves the next equation and meets both of its conditions, so the solution of that
boundary value problem is not unique and it has no Green's function.

```
>> GreenFunction({u''(x) + u(x), u(0) == 0, u(Pi) == 0}, u(x), {x, 0, Pi}, s)
GreenFunction({u(x)+u''(x),u(0)==0,u(Pi)==0},u(x),{x,0,Pi},s)
```

### Related terms
[DSolve](DSolve.md), [DiracDelta](DiracDelta.md), [HeavisideTheta](HeavisideTheta.md), [Wronskian](Wronskian.md)

### Implementation status

* &#x2611; - partially implemented

## DSolve

```
DSolve(equation, f(var), var)
```
> attempts to solve a differential `equation` for the function `f(var)` and variable `var`.

```
DSolve(equations, {f(var), g(var)}, var)
```
> attempts to solve a system of differential `equations`.

```
DSolve(equation, u(var1, var2), {var1, var2})
```
> attempts to solve a partial differential `equation` in two independent variables.

The unknown function and the variable it depends on may be left out when the equation determines
them. Boundary and initial conditions are given alongside the equation. The arbitrary constants
and functions of a general solution are named `C(1)`, `C(2)`, ..., which the option
`GeneratedParameters` renames.

Ordinary differential equations are solved by the characteristic polynomial and variation of
parameters when they have constant coefficients, and otherwise by the methods for Cauchy-Euler,
separable, exact, homogeneous, Bernoulli, Riccati and Clairaut equations, by reduction of order,
by the Laplace transform, by substitutions which make a first order equation linear, and by
recognizing the equations of the Airy, Bessel, Legendre and hypergeometric functions, in the
variable they are written in or in another one. A nonlinear equation of the second order which
none of those answers is reduced through a symmetry of it. Systems are solved when they have
constant coefficients, after carrying the higher derivatives as unknowns of their own if need be,
when their matrix commutes with its own integral, and, for two equations without the variable in
them, through the curve their solutions trace out.
Partial differential equations in two independent variables are solved by the method of
characteristics when they are linear or quasi-linear, and receive a complete integral through
Clairaut's and Charpit's methods when they are nonlinear.

See:  
* [Wikipedia - Ordinary differential equation](https://en.wikipedia.org/wiki/Ordinary_differential_equation)
* [Wikipedia - Partial differential equation](https://en.wikipedia.org/wiki/Partial_differential_equation)

### Examples

```
>> DSolve({y'(x)==y(x)+2},y(x), x)
{{y(x)->-2+E^x*C(1)}}

>> DSolve({y'(x)==y(x)+2,y(0)==1},y(x), x)
{{y(x)->-2+3*E^x}}

>> DSolve(y''(x) == 0, y(x), x)
{{y(x)->C(1)+x*C(2)}}     

>> DSolve(y''(x) == y(x), y(x), x)
{{y(x)->C(1)/E^x+E^x*C(2)}}

>> DSolve(y''(x) == y(x), y, x)
{{y->Function({x},C(1)/E^x+E^x*C(2))}}        
```

The unknown and the variable may be left out, and an equation written without arguments is
answered with a pure function.

```
>> DSolve(y'(x) == y(x))
{{y(x)->E^x*C(1)}}

>> DSolve(y' == y)
{{y->Function({x},E^x*C(1))}}
```

Equations of higher order with constant coefficients are solved through the roots of their
characteristic polynomial, and an inhomogeneous one by variation of parameters.

```
>> DSolve(y'''(x) - 6*y''(x) + 11*y'(x) - 6*y(x) == 0, y(x), x)
{{y(x)->E^x*C(1)+E^(2*x)*C(2)+E^(3*x)*C(3)}}

>> DSolve(y''(x) + y(x) == Sec(x), y(x), x)
{{y(x)->C(1)*Cos(x)+Cos(x)*Log(Cos(x))+x*Sin(x)+C(2)*Sin(x)}}
```

The equations of the named functions are recognized from the shape of their coefficients, and
those coefficients need only become rational in another variable.

```
>> DSolve((1-x^2)*y''(x) - 2*x*y'(x) + 15/4*y(x) == 0, y(x), x)
{{y(x)->C(1)*LegendreP(3/2,x)+C(2)*LegendreQ(3/2,x)}}

>> DSolve(y''(x) - x^4*y(x) == 0, y(x), x)
{{y(x)->Sqrt(x)*BesselI(1/6,x^3/3)*C(1)+Sqrt(x)*BesselK(1/6,x^3/3)*C(2)}}

>> DSolve(x*y''(x) + (b - x)*y'(x) - a*y(x) == 0, y(x), x)
{{y(x)->C(1)*Hypergeometric1F1(a,b,x)+x^(1-b)*C(2)*Hypergeometric1F1(1+a-b,2-b,x)}}
```

The solutions of a third order equation can be the products of the solutions of a second order
one, and a substitution can make a first order equation linear.

```
>> DSolve(y'''(x) - 4*(x + 2)*y'(x) - 2*y(x) == 0, y(x), x)
{{y(x)->AiryAi(2+x)^2*C(1)+AiryAi(2+x)*AiryBi(2+x)*C(2)+AiryBi(2+x)^2*C(3)}}

>> DSolve(y'(x) == y(x)*(E^x + Log(y(x))), y(x), x)
{{y(x)->E^(E^x*x+E^x*C(1))}}
```

A nonlinear equation of the second order is reduced through a symmetry of it, in coordinates in
which that symmetry is a translation.

```
>> DSolve(x^3*y''(x) == (y(x) - x*y'(x))^2, y(x), x)
{{y(x)->x*C(1)+x*Log(x)-x*Log(1+x*C(2))}}
```

`GeneratedParameters` names the arbitrary constants.

```
>> DSolve(y''(x) - 4*y(x) == 0, y(x), x, GeneratedParameters -> f)
{{y(x)->f(1)/E^(2*x)+E^(2*x)*f(2)}}
```

Systems are split into blocks which share no unknown, and each block is solved on its own.

```
>> DSolve({y'(x) == x^2*y(x), z'(x) == 5*z(x)}, {y, z}, x)
{{y->Function({x},E^(x^3/3)*C(1)),z->Function({x},E^(5*x)*C(2))}}

>> DSolve({x'(t)==y(t), y'(t)==-x(t)}, {x(t), y(t)}, t)
{{x(t)->C(1)*Cos(t)+C(2)*Sin(t),y(t)->C(2)*Cos(t)-C(1)*Sin(t)}}
```

A system whose matrix depends on the variable is solved when that matrix commutes with its own
integral, and one of two equations without the variable in them through the curve its solutions
trace out, which is the one shape of nonlinear system answered here.

```
>> DSolve({x'(t) == x(t)/t + y(t), y'(t) == -x(t) + y(t)/t}, {x(t), y(t)}, t)
{{x(t)->t*C(1)*Cos(t)+t*C(2)*Sin(t),y(t)->t*C(2)*Cos(t)-t*C(1)*Sin(t)}}

>> DSolve({x'(t) == y(t), y'(t) == y(t)^2/x(t)}, {x(t), y(t)}, t)
{{x(t)->E^(t*C(2))*C(1),y(t)->E^(t*C(2))*C(1)*C(2)}}
```

The general solution of a partial differential equation contains an arbitrary function rather than
an arbitrary constant.

```
>> DSolve(D(f(x, y), x)/f(x, y) + 3*D(f(x, y), y) / f(x, y) == 2, f, {x, y}) 
{{f->Function({x,y},E^(2*x)*C(1)[-3*x+y])}}

>> DSolve(D(f(x, y), x)*x + D(f(x, y), y)*y == 2, f(x, y), {x, y}) 
{{f(x,y)->2*Log(x)+C(1)[y/x]}}
        
>> DSolve(D(y(x, t), t) + 2*D(y(x, t), x) == 0, y(x, t), {x, t}) 
{{y(x,t)->C(1)[1/2*(2*t-x)]}}

>> DSolve(D(u(x, y), x) == 1, u(x,y), {x, y})
{{u(x,y)->x+C(1)[y]}}
```

Homogeneous equations of second order with constant coefficients are solved through the roots of
their principal part. Laplace's equation has imaginary characteristic directions, the wave equation
two real ones, and a repeated direction contributes a factor of the variable.

```
>> DSolve(D(u(x,y), {x,2}) + D(u(x,y), {y,2}) == 0, u(x,y), {x, y})
{{u(x,y)->C(1)[-I*x+y]+C(2)[I*x+y]}}

>> DSolve(D(u(x,t), {x,2}) - D(u(x,t), {t,2}) == 0, u(x,t), {t, x})
{{u(x,t)->C(1)[-t+x]+C(2)[t+x]}}

>> DSolve(3*D(u(x,y),{x,2}) + 30*D(u(x,y),x,y) + 75*D(u(x,y),{y,2}) == 0, u, {x, y})
{{u->Function({x,y},C(1)[-5*x+y]+x*C(2)[-5*x+y])}}
```

A nonlinear equation of first order has no general solution in terms of an arbitrary function.
What is returned is a complete integral, a family with two parameters.

```
>> DSolve(u(x,y) == x*D(u(x,y),x) + y*D(u(x,y),y) + Sin(D(u(x,y),x) + D(u(x,y),y)), u, {x, y})
{{u->Function({x,y},x*C(1)+y*C(2)+Sin(C(1)+C(2)))}}
```

A condition which prescribes the unknown along a line determines the arbitrary function, so an
initial profile is carried along the characteristics.

```
>> DSolve({D(u(t,x),t) + c*D(u(t,x),x) == 0, u(0,x) == E^(-x^2)}, u, {t, x})
{{u->Function({t,x},E^(-(-c*t+x)^2))}}
```

### Related terms
[DSolveValue](DSolveValue.md), [Factor](Factor.md), [FindRoot](FindRoot.md), [NDSolve](NDSolve.md), [NRoots](NRoots.md),[Solve](Solve.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DSolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/DSolve.java#L60) 

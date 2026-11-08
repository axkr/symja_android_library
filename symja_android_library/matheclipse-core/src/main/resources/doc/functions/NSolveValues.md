## NSolveValues

```
NSolveValues(equations, vars)
```

> attempts to solve `equations` for the variables `vars` numerically and returns the values of the variables instead of a list of rules.

```
NSolveValues(equations, vars, domain)
```

> attempts to solve `equations` numerically for the variables `vars` in the given `domain`.

```
NSolveValues(equations, vars, domain, precision)
```

> attempts to solve `equations` numerically with the given working `precision`.

`NSolveValues` solves the equations like [NSolve](NSolve.md) and replaces each solution by the values it gives the variables:

* for a single variable `x` the result is the list of the values of `x`
* for a list of variables `{x, y, ...}` the result is a list of lists `{x0, y0, ...}`, one for each solution, in the order of the variables

A bare expression `expr` is the equation `expr == 0`. The solutions of a univariate polynomial equation are ordered by their real part and, for equal real parts, by their imaginary part, and a root of multiplicity `k` is returned `k` times. An equation without solutions gives the empty list `{}`.

### Options

- `MaxRoots` - the maximum number of roots, which should be returned
- `WorkingPrecision` - the number of significant digits which should be used for the numerical solution; the default value `Automatic` uses machine precision

### Examples

```
>> NSolveValues(x^2==2, x)
{-1.41421,1.41421}

>> NSolveValues(x^2-2, x)
{-1.41421,1.41421}

>> NSolveValues({x+y==3, x-y==1}, {x,y})
{{2.0,1.0}}

>> NSolveValues({x^2+y^2==1, x==y}, {x,y})
{{-0.707107,-0.707107},{0.707107,0.707107}}
```

The root `0` of `x^3-4*x^2` has multiplicity `2`:

```
>> NSolveValues(x^3-4*x^2==0, x)
{0.0,0.0,4.0}
```

All five roots of a quintic, and only its real root:

```
>> NSolveValues(x^5-x+1==0, x)
{-1.1673,-0.181232+I*(-1.08395),-0.181232+I*1.08395,0.764884+I*(-0.352472),0.764884+I*0.352472}

>> NSolveValues(x^5-x+1==0, x, Reals)
{-1.1673}
```

A higher working precision:

```
>> NSolveValues(x^2==2, x, Reals, 30)
{-1.41421356237309504880168872421,1.41421356237309504880168872421}

>> NSolveValues(x^2==2, x, WorkingPrecision->30)
{-1.41421356237309504880168872421,1.41421356237309504880168872421}
```

### Related terms
[NSolve](NSolve.md), [NRoots](NRoots.md), [Solve](Solve.md), [SolveValues](SolveValues.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of NSolveValues](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NSolveValues.java)

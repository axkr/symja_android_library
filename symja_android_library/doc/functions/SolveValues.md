## SolveValues

```
SolveValues(equations, vars)
```

> attempts to solve `equations` for the variables `vars` and returns the values of the variables instead of a list of rules.

```
SolveValues(equations, vars, domain)
```

> attempts to solve `equations` for the variables `vars` in the given `domain`.

`SolveValues` solves the equations like [Solve](Solve.md) and replaces each solution by the values it gives the variables:

* for a single variable `x` the result is the list of the values of `x`
* for a list of variables `{x, y, ...}` the result is a list of lists `{x0, y0, ...}`, one for each solution, in the order of the variables

A bare expression `expr` is the equation `expr == 0`. A root of multiplicity `k` is returned `k` times. An equation without solutions gives the empty list `{}`. The `domain` is a domain like `Reals` or `Integers`; by default the solutions are complex.

`SolveValues` has the same options as [Solve](Solve.md).

### Examples

```
>> SolveValues(x^2==1, x)
{-1,1}

>> Solve(x^2==1, x)
{{x->-1},{x->1}}

>> SolveValues(x^2-3*x+2==0, x)
{1,2}

>> SolveValues(a*x+b==0, x)
{-b/a}
```

A list of variables gives a list of values for each solution:

```
>> SolveValues({x+y==2, x-y==0}, {x,y})
{{1,1}}

>> SolveValues({x^2+y^2==1, x==y}, {x,y})
{{-1/Sqrt(2),-1/Sqrt(2)},{1/Sqrt(2),1/Sqrt(2)}}

>> SolveValues(x^2==1, {x})
{{-1},{1}}
```

The root `1` of `(x-1)^2` has multiplicity `2`:

```
>> SolveValues((x-1)^2==0, x)
{1,1}
```

Solutions in a domain:

```
>> SolveValues(x^2==-1, x)
{-I,I}

>> SolveValues(x^2==-1, x, Reals)
{}

>> SolveValues(x^3==1, x, Reals)
{1}

>> SolveValues(x^4==16, x, Integers)
{-2,2}
```

Conditions on the variables and options of `Solve`:

```
>> SolveValues(x^2==4 && x>0, x)
{2}

>> SolveValues(x^2==1, x, Modulus->3)
{1,2}
```

A solution which holds under a condition:

```
>> SolveValues(Exp(x)==2, x)
{ConditionalExpression(I*2*Pi*C(1)+Log(2),C(1)∈Integers)}
```

### Related terms
[NSolveValues](NSolveValues.md), [Reduce](Reduce.md), [Roots](Roots.md), [Solve](Solve.md), [Values](Values.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of SolveValues](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/SolveValues.java)

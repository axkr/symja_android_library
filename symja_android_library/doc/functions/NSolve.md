## NSolve 

```
NSolve(equations, vars)
```

> attempts to solve `equations` for the variables `vars`.

```
NSolve(equations, vars, domain)
```

> attempts to solve `equations` for the variables `vars` in the given `domain`.

```
NSolve(equations, vars, domain, precision)
```

> attempts to solve `equations` with the given working `precision`.

**Note:** `NSolve` calls [Solve](Solve.md) in numeric mode.

A univariate polynomial equation is solved numerically, so that the solutions are machine numbers for every degree. A root of multiplicity `k` is returned `k` times and the solutions are ordered by their real part and, for equal real parts, by their imaginary part. A working precision beyond machine precision is answered by evaluating the exact solutions with the requested number of digits.

### Options

- `MaxRoots` the maximum number of roots, which should be returned
- `WorkingPrecision` - the number of significant digits which should be used for the numerical solution; the default value `Automatic` uses machine precision 

### Examples

It's important to use the `==` operator to define the equations. If you have unintentionally assigned a value to the variables `x, y` with the `=` operator you have to call `Clear(x,y)` to clear the definitions for these variables.

```
>> NSolve({Sin(x)-11==y, x+y==-9}, {y,x})
{x->1.1060601577062719,y->-10.106060157706272}
```

A bare expression is the equation `expr == 0`.

```
>> NSolve({x+y-3, x-y-1}, {x,y})
{{x->2.0,y->1.0}}
```

The root `0` of `x^3-4*x^2` has multiplicity `2`.

```
>> NSolve(x^3-4.*x^2==0, x)
{{x->0.0},{x->0.0},{x->4.0}}
```

### Related terms 
[NSolveValues](NSolveValues.md), [Solve](Solve.md), [SolveValues](SolveValues.md), [Reduce](Reduce.md), [Roots](Roots.md) 


### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of NSolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NSolve.java#L16) 

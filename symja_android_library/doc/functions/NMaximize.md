## NMaximize

``` 
NMaximize({maximize_function, constraints}, variables_list)
```

> the `NMaximize` function provides an implementation of [George Dantzig's simplex algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization problems with linear equality and inequality constraints. The variables are free, as in Mathematica: a non-negative variable needs its constraint `x >= 0`. For a non-linear function with constraints the local constrained search of `FindMaximum` is used from several start points.

> A non-linear function without constraints, or with bounds for every variable, is searched for its global maximum: a population search (differential evolution with fixed random numbers, so a call gives the same answer every time) runs beside the local search, and the better result is returned. A function which is unbounded gives the message `ubnd` and `{Infinity, {x -> Indeterminate}}`.

See:  
* [Wikipedia - Linear programming](http://en.wikipedia.org/wiki/Linear_programming)

### Examples

```
>> NMaximize({-2*x+y-5, x+2*y<=6 && 3*x + 2*y <= 12 && x >= 0 && y >= 0}, {x, y})
{-2.0,{x->0.0,y->3.0}}
```

solves the linear problem:

```
Maximize -2x + y - 5
```

with the constraints:

```
  x  + 2y <=  6
  3x + 2y <= 12
        x >= 0
		y >= 0
```

### Related terms 
[LinearProgramming](LinearProgramming.md), [NMinimize](NMinimize.md)






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of NMaximize](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/MinMaxFunctions.java#L1725) 

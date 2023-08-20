## LinearProgramming

```
LinearProgramming(coefficientsOfLinearObjectiveFunction, constraintList, constraintRelationList)
```

> the `LinearProgramming` function provides an implementation of [George Dantzig's simplex algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization problems with linear equality and inequality constraints  and implicit non-negative variables.

See
* [Wikipedia - Linear programming](http://en.wikipedia.org/wiki/Linear_programming)

### Examples

```
>> LinearProgramming({-2, 1, -5}, {{1, 2, 0},{3, 2, 0},{0,1,0},{0,0,1}}, {{6,-1},{12,-1},{0,1},{1,0}})
{4.0,0.0,1.0} 
```

solves the linear problem:

```
Minimize -2x + y - 5
```

with the constraints:

```
  x  + 2y <=  6
  3x + 2y <= 12
        x >= 0
		y >= 0
```

### Related terms
[NMaximize](NMaximize.md), [NMinimize](NMinimize.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LinearProgramming](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/LinearProgramming.java#L78) 

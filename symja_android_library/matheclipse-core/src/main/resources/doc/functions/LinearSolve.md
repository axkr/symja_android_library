## LinearSolve

```
LinearSolve(matrix, right)
```

> solves the linear equation system 'matrix . x = right' and returns one corresponding solution `x`.

See
* [Wikipedia - System of linear equations - Solving a linear system](https://en.wikipedia.org/wiki/System_of_linear_equations#Solving_a_linear_system)

### Examples	

```
>> LinearSolve({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}}, {1, 2, 3})
{0,1,2}
```

Test the solution:

```
>> {{1, 1, 0}, {1, 0, 1}, {0, 1, 1}} . {0, 1, 2}
{1,2,3}
```

If there are several solutions, one arbitrary solution is returned:

```
>> LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, 1, 1})
{-1,1,0}
```

Infeasible systems are reported:

```
>> LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, -2, 3}) 
 
LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, -2, 3})
```

Argument {1, {2}} at position 1 is not a non-empty rectangular matrix.

```
>> LinearSolve({1, {2}}, {1, 2})
LinearSolve({1, {2}}, {1, 2})

>> LinearSolve({{1, 2}, {3, 4}}, {1, {2}}) 
LinearSolve({{1, 2}, {3, 4}}, {1, {2}})
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LinearSolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L3147) 

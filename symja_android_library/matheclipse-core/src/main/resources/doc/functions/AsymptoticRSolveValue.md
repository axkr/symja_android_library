## AsymptoticRSolveValue

```
AsymptoticRSolveValue(equation, f(x), {x,a,order})
```

> returns an approximation for the difference `equation` for the function `f(x)` and variable `x` to order `order`.
 
### Examples

```
>> AsymptoticRSolveValue({a(n) == 3*a(n - 1), a(0) == 5}, a(n), {n, Infinity, 3})
5*3^n
```
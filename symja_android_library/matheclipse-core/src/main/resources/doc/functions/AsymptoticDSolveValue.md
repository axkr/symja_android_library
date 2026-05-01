## AsymptoticDSolveValue

```
AsymptoticDSolveValue(equation, f(x), {x,a,order})
```

> returns an approximation of order `order` for the differential `equation` for the function `f(x)` and variable `x`.

See:  
* [Wikipedia - Asymptotic analysis](https://en.wikipedia.org/wiki/Asymptotic_analysis)

### Examples

```
>> AsymptoticDSolveValue({y'(x) == y(x), y(0) == 1}, y(x), {x, 0, 3})
1+x+x^2/2+x^3/6
```
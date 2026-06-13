## Asymptotic

```
Asymptotic(expression,{x,a,order})
```

> returns an asymptotic approximation for the `expression` near `a` to order `order`.

### Examples

```
>> Asymptotic(Tan(x), {x, 0, 5})
x+x^3/3+2/15*x^5
```
## AsymptoticSolve

```
AsymptoticSolve(equation, v->b, {x,a,order})
```

> returns an asymptotic approximation for the `equation` to order `order`.

### Examples

```
>> AsymptoticSolve(y^2 + y - 2 - x == 0, y -> 1, {x, 0, 2}) 
{{y->1+x/3-x^2/27}}
```
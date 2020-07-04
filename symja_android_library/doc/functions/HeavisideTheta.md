## HeavisideTheta

```
HeavisideTheta(expr1, expr2, ... exprN)
```

> returns `1` if all `expr1, expr2, ... exprN` are positive and `0` if one of the `expr1, expr2, ... exprN` is negative. `HeavisideTheta(0)` returns unevaluated as `HeavisideTheta(0)`.

### Examples

```
>> HeavisideTheta[42]
1
```
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






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of HeavisideTheta](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/HeavisideTheta.java#L15) 

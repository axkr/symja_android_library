## Nest

```
Nest(f, expr, n)
```
> starting with `expr`, iteratively applies `f` `n` times and returns the final result.

### Examples
 
```
>> Nest(f, x, 3)
f(f(f(x)))
 
>> Nest((1+#) ^ 2 &, x, 2)
(1+(1+x)^2)^2
```

### Related terms 
[FixedPoint](FixedPoint.md), [FixedPointList](FixedPointList.md), [NestWhile](NestWhile.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Nest](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L1513) 

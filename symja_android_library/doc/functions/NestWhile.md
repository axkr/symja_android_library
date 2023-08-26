## NestWhile

```
NestWhile(f, expr, test)
```
> applies a function `f` repeatedly on an expression `expr`, until applying `test` on the result no longer yields `True`.

```
NestWhile(f, expr, test, m)
```
> supplies the last `m` results to `test` (default value: `1`).
	
```
NestWhile(f, expr, test, All)
```
> supplies all results gained so far to `test`.

### Examples

Divide by 2 until the result is no longer an integer:

``` 
>> NestWhile(#/2&, 10000, IntegerQ)
625/2
```

### Related terms 
[FixedPoint](FixedPoint.md), [FixedPointList](FixedPointList.md), [Nest](Nest.md) 










### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NestWhile](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L1653) 

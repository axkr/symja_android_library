## TrigExpand

```
TrigExpand(expr)
```

> expands out trigonometric expressions in `expr`.

### Examples

```
>> TrigExpand(Sin(x+y))
Cos(x)*Sin(y)+Cos(y)*Sin(x)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TrigExpand](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/TrigExpand.java#L47) 

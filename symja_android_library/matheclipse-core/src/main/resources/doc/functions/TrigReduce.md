## TrigReduce

```
TrigReduce(expr)
```

> rewrites products and powers of trigonometric functions in `expr` in terms of trigonometric functions with combined arguments.
 
### Examples

```
>> TrigReduce(2*Sin(x)*Cos(y))
Sin(-y+x)+Sin(y+x)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TrigReduce](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/TrigReduce.java#L54) 

## CheckAbort

```
CheckAbort(expr, failure-expr)
```

> evaluates `expr`, and returns the result, unless `Abort` was called during the evaluation, in which case `failure-expr` will be returned.

### Examples
 
```
>> CheckAbort(Abort(); -1, 41) + 1 
42

>> CheckAbort(abc; -1, 41) + 1
0
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CheckAbort](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L416) 

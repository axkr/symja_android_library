## AbsoluteTiming

```
AbsoluteTiming(x)
```

> returns a list with the first entry containing the evaluation time of `x` and the second entry is the evaluation result of `x`.

### Examples

```
>> AbsoluteTiming(x = 1; Pause(x); x + 3)[[1]] > 1
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AbsoluteTiming](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L166) 

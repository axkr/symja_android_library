## Last

```
Last(expr)
```

> returns the last element in `expr`.

### Examples

`Last(expr)` is equivalent to `expr[[-1]]`.

```
>> Last({a, b, c})
c
```

Nonatomic expression expected.

```
>> Last(x)
Last(x)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Last](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L3911) 

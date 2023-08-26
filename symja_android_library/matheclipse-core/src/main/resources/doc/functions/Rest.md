## Rest

```
Rest(expr)
```

> returns `expr` with the first element removed.

`Rest(expr)` is equivalent to `expr[[2;;]]`.

### Examples

```
>> Rest({a, b, c})
{b,c}
 
>> Rest(a + b + c)
b+c
```

Nonatomic expression expected.

```
>> Rest(x)
Rest(x)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Rest](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L6204) 

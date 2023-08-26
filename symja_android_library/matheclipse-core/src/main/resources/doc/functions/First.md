## First

```
First(expr)
```
> returns the first element in `expr`.

### Examples

`First(expr)` is equivalent to `expr[[1]]`.
```
>> First({a, b, c})
a
 
>> First(a + b + c)
a
```

Nonatomic expression expected.
```
>> First(x)
First(x)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of First](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L2860) 

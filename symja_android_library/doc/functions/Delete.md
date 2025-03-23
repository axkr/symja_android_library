## Delete

```
Delete(expr, n)
```
      
> deletes the element at position `n` in `expr`. The position is counted from the end if `n` is negative.

```
Delete(expr, {m,n, ...})
```

> deletes the element at position `{m, n, ...}`.

### Examples

```
>> Delete({a, b, c, d}, 3)
{a,b,d}

>> Delete({a, b, c, d}, -2)
{a,b,d}
```
 






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Delete](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L2100) 

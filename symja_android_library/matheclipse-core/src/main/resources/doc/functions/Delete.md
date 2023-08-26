## Delete

```
Delete(expr, n)
```

> returns `expr` with part `n` removed. 


### Examples

```
>> Delete({a, b, c, d}, 3)
{a,b,d}

>> Delete({a, b, c, d}, -2)
{a,b,d}
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Delete](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L1995) 

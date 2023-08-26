## Most

```
Most(expr)
```

> returns `expr` with the last element removed.

`Most(expr)` is equivalent to `expr[[;;-2]]`.

### Examples

```
>> Most({a, b, c})
{a,b}
 
>> Most(a + b + c)
a+b
```

Nonatomic expressions are expected.

```
>> Most(x) 
Most(x)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Most](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L4268) 

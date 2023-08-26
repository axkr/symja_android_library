## And

```
And(expr1, expr2, ...) 
```

> `expr1 && expr2 && ...` evaluates each expression in turn, returning `False` as soon as an expression evaluates to `False`. If all expressions evaluate to `True`, `And` returns `True`.
 
### Examples

```
>> True && True && False
False
```

If an expression does not evaluate to `True` or `False`, `And` returns a result in symbolic form:

```
>> a && b && True && c
a && b && c
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of And](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L677) 

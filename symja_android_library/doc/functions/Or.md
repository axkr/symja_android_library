## Or

```
Or(expr1, expr2, ...)'
```

> `expr1 || expr2 || ...` evaluates each expression in turn, returning `True` as soon as an expression evaluates to `True`. If all expressions evaluate to `False`, `Or` returns `False`.

See
* [Wikipedia - Logical disjunction](https://en.wikipedia.org/wiki/Logical_disjunction)

### Examples

```
>> False || True
True
``` 

If an expression does not evaluate to `True` or `False`, `Or` returns a result in symbolic form:
``` 
>> a || False || b
a || b
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Or](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L3889) 

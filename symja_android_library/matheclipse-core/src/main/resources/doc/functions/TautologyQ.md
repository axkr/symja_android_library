## TautologyQ

```
TautologyQ(boolean-expr, list-of-variables)
```

> test whether the `boolean-expr` is satisfiable by all combinations of boolean `False` and `True` values for the `list-of-variables`.
 
See
* [Wikipedia - Tautology (logic)](https://en.wikipedia.org/wiki/Tautology_(logic))

### Examples

```
>> TautologyQ(a || !a) 
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TautologyQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L4437) 

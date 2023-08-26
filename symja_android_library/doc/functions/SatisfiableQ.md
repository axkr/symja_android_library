## SatisfiableQ

```
SatisfiableQ(boolean-expr, list-of-variables)
```

> test whether the `boolean-expr` is satisfiable by a combination of boolean `False` and `True` values for the `list-of-variables`.

See
* [Wikipedia - Boolean satisfiability problem](https://en.wikipedia.org/wiki/Boolean_satisfiability_problem)

### Examples

```
>> SatisfiableQ((a || b) && (! a || ! b), {a, b})
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SatisfiableQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L4306) 

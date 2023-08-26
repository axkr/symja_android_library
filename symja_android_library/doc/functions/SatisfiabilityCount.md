## SatisfiabilityCount

```
SatisfiabilityCount(boolean-expr)
```

> test whether the `boolean-expr` is satisfiable by a combination of boolean `False` and `True` values for the  variables of the boolean expression and return the number of possible combinations.

```
SatisfiabilityCount(boolean-expr, list-of-variables)
```

> test whether the `boolean-expr` is satisfiable by a combination of boolean `False` and `True` values for the `list-of-variables` and return the number of possible combinations.


See
* [Wikipedia - Boolean satisfiability problem](https://en.wikipedia.org/wiki/Boolean_satisfiability_problem)

### Examples

```
>> SatisfiabilityCount((a || b) && (! a || ! b), {a, b})
2
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SatisfiabilityCount](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L4143) 

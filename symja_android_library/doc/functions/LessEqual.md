## LessEqual

```
LessEqual(x, y) 

x <= y
```

> yields `True` if `x` is known to be less than or equal `y`.

```
lhs <= rhs
```

> represents the inequality `lhs ≤ rhs`.
 
	
### Examples
 
```
>> 3<=4
True

>> {LessEqual(), LessEqual(x), LessEqual(1)}
{True, True, True}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LessEqual](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L2849) 

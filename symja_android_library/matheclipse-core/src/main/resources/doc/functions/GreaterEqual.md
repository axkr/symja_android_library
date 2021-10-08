## GreaterEqual

```
GreaterEqual(x, y) 

x >= y
```

> yields `True` if `x` is known to be greater than or equal to `y`.

```
lhs >= rhs
```

> represents the inequality `lhs >= rhs`.
 
	
### Examples
 
```
>> x>=x
True

>> {GreaterEqual(), GreaterEqual(x), GreaterEqual(1)}
{True, True, True}
```

### Github

* [Implementation of GreaterEqual](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L1997) 

## Minimize

```
Minimize(unary-function, variable) 
```

> returns the minimum of the unary function for the given `variable`.
	
See:
* [Wikipedia - Derivative test](https://en.wikipedia.org/wiki/Derivative_test)
	
### Examples

```
>> Minimize(x^4+7*x^3-2*x^2 + 42, x) 
{42+7*(-21/8-Sqrt(505)/8)^3-2*(21/8+Sqrt(505)/8)^2+(21/8+Sqrt(505)/8)^4,{x->-21/8-Sqrt(505)/8}}
```

### Option

```
Minimize(..., Method -> "ORTools")
```

> solves a linear problem with the Glop simplex solver of OR-Tools, over the `Integers` with its branch and bound. It needs the module `matheclipse-or-tools`; without it the default method is used.

### Related terms 
[Maximize](Maximize.md) 






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Minimize](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/MinMaxFunctions.java#L1582) 

## ConditionalExpression

```
ConditionalExpression(expr, condition)
```

> if `condition` evaluates to `True` return `expr`, if `condition` evaluates to `False` return `Undefined`. Otherwise return the `ConditionalExpression` unevaluated.
   
### Examples
 

```
>> ConditionalExpression(a, True) 
a

>> ConditionalExpression(a, False)
Undefined
				
>> Limit(x^(a/x), x->Infinity) 
ConditionalExpression(1,Element(a,Reals))
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ConditionalExpression](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L893) 

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

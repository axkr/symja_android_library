## ConditionalExpression

```
ConditionalExpression(expr, condition)
```

> if `condition` evaluates to `True` return `expr`, if `condition` evaluates to `False` return `Undefined`. Otherwise return the `ConditionalExpression` unevaluated.
   
### Examples

The controlling expression of a `Condition` can use variables from the pattern:

```
>> Limit(x^(a/x), x->Infinity) 
ConditionalExpression(1,Element(a,Reals))
```

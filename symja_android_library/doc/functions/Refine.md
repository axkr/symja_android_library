## Refine

```
Refine(expression, assumptions)
```
> evaluate the `expression` for the given `assumptions`.
 

### Examples
```
>> Refine(Abs(n+Abs(m)), n>=0)
Abs(m)+n

>> Refine(-Infinity<x, x>0)
True

>> Refine(Max(Infinity,x,y), x>0)
Max(Infinity,y)
```  

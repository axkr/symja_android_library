## ExpandNumerator

```
ExpandNumerator(expr)
```
 
> expands the numerator of `expr`.

### Examples

```
>> ExpandNumerator((x+y)*(x-y)/((x+1)*y))
(x^2-y^2)/((1+x)*y)
```

### Related terms 
[Expand](Expand.md), [ExpandAll](ExpandAll.md), [ExpandDenominator](ExpandDenominator.md)
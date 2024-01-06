## ExpandDenominator

```
ExpandDenominator(expr)
```
 
> expands the denominator of `expr`.

### Examples

```
>> ExpandDenominator((x+y)*(x-y)/((x+1)*y))
((x-y)*(x+y))/(y+x*y)
```

### Related terms 
[Expand](Expand.md), [ExpandAll](ExpandAll.md), [ExpandNumerator](ExpandNumerator.md)
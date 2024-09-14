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

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ExpandDenominator](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L2070) 

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

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ExpandNumerator](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L2099) 

## FactorTermsList

```
FactorTermsList(poly)
```

> pulls out any overall numerical factor in `poly` and returns the result in a list.

### Examples

```
>> FactorTermsList(3+3/4*x^3+12/17*x^2)
{3/68,68+16*x^2+17*x^3}
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FactorTermsList](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L2818) 

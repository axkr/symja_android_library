## FactorTerms 

```
FactorTerms(poly)
```

> pulls out any overall numerical factor in `poly`.

### Examples

```
>> FactorTerms(3+3/4*x^3+12/17*x^2, x)
3/68*(17*x^3+16*x^2+68)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FactorTerms](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L2727) 

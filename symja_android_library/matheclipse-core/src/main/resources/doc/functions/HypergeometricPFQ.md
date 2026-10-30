## HypergeometricPFQ

```
HypergeometricPFQ({a,...}, {b,...}, c)
```

> return the `HypergeometricPFQ` function

See:
* [Wikipedia - Generalized hypergeometric function](https://en.wikipedia.org/wiki/Generalized_hypergeometric_function)

### Examples

```
>> HypergeometricPFQ({1,2,3},{4,5},0.5)
1.18987

>> HypergeometricPFQ({1,1},{2},x)
-Log(1-x)/x
```

With no parameters the series is the exponential function:

```
>> HypergeometricPFQ({}, {}, x)
E^x
```






### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of HypergeometricPFQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/HypergeometricFunctions.java#L1649) 

* [Rule definitions of HypergeometricPFQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/HypergeometricPFQRules.m) 

## Hypergeometric2F1

```
Hypergeometric2F1(a, b, c, z)
```

> return the `Hypergeometric2F1` function

See:
* [Wikipedia - Hypergeometric function](https://en.wikipedia.org/wiki/Hypergeometric_function)
* [Wikipedia - Generalized hypergeometric function](https://en.wikipedia.org/wiki/Generalized_hypergeometric_function)
* [Fungrim - Gauss hypergeometric function](http://fungrim.org/topic/Gauss_hypergeometric_function/)

### Examples

```
>> Hypergeometric2F1(2, 3, 4, 0.5)
2.72894

>> Hypergeometric2F1(1, 1, 2, x)
-Log(1-x)/x

>> Hypergeometric2F1(1/2, 1/2, 3/2, x^2)
ArcSin(x)/x
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Hypergeometric2F1](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/HypergeometricFunctions.java#L1381) 

* [Rule definitions of Hypergeometric2F1](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/Hypergeometric2F1Rules.m) 

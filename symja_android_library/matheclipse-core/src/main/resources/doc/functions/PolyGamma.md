## PolyGamma

 
```
PolyGamma(value)
```

> return the digamma function of the `value`. The digamma function is defined as the logarithmic derivative of the gamma function.

```
PolyGamma(m, value)
```

> return the polygamma function of order `m` of the `value`. The polygamma function of order `m` is a meromorphic function on the complex numbers ℂ  defined as the (m + 1)-th derivative of the logarithm of the gamma function.
  
See
* [Wikipedia: Polygamma function](https://en.wikipedia.org/wiki/Polygamma_function)
* [Wikipedia: Digamma function](https://en.wikipedia.org/wiki/Digamma_function)
* [Fungrim - Digamma function](http://fungrim.org/topic/Digamma_function/)

### Examples

```
>> PolyGamma(-1, 1)
0
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PolyGamma](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java#L1487) 

* [Rule definitions of PolyGamma](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/PolyGammaRules.m) 

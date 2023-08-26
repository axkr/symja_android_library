## Gamma

```
Gamma(z) 
```

> is the gamma function on the complex number `z`.

```
Gamma(z, x) 
```

> is the upper incomplete gamma function.
 
```
Gamma(z, x0, x1) 
```

> is equivalent to `Gamma(z, x0) - Gamma(z, x1)`. 
      
See
* [Wikipedia - Gamma function](https://en.wikipedia.org/wiki/Gamma_function) 
* [Fungrim - Gamma function](http://fungrim.org/topic/Gamma_function/)
* [Wikipedia - Incomplete gamma function](https://en.wikipedia.org/wiki/Incomplete_gamma_function)

### Examples

```
>> Gamma(8)
5040

>> Gamma(1/2)
Sqrt(Pi)

>> Gamma(2.2)
1.1018024908797128
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Gamma](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L1546) 

* [Rule definitions of Gamma](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/GammaRules.m) 

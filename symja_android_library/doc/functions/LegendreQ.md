## LegendreQ

```
LegendreQ(n, x)
```

> returns the Legendre functions of the second kind `Q_n(x)`.
 
See  
* [Wikipedia - Legendre polynomials](https://en.wikipedia.org/wiki/Legendre_polynomials)

### Examples

```
>> Expand(LegendreQ(4,z))   
55/24*z-35/8*z^3-3/16*Log(1-z)+15/8*z^2*Log(1-z)-35/16*z^4*Log(1-z)+3/16*Log(1+z)-15/8*z^2*Log(1+z)+35/16*z^4*Log(1+z)
```
   






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LegendreQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PolynomialFunctions.java#L1823) 

* [Rule definitions of LegendreQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/LegendreQRules.m) 

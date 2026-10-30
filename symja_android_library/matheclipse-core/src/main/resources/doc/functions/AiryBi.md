## AiryBi

```
AiryBi(z)
```

> returns the Airy function of the second kind of `z`.

See
* [Wikipedia - Airy function](https://en.wikipedia.org/wiki/Airy_function)
* [Fungrim - Airy functions](http://fungrim.org/topic/Airy_functions/)

### Examples

```
>> AiryBi(1.8)
2.59587
```

`AiryBi(0)` has a closed form:

```
>> AiryBi(0)
1/(3^(1/6)*Gamma(2/3))
```

The derivative is the `AiryBiPrime` function:

```
>> D(AiryBi(x),x)
AiryBiPrime(x)
```







### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of AiryBi](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BesselFunctions.java#L303) 

## AiryAi

```
AiryAi(z)
```

> returns the Airy function of the first kind of `z`.

See
* [Wikipedia - Airy function](https://en.wikipedia.org/wiki/Airy_function)
* [Fungrim - Airy functions](http://fungrim.org/topic/Airy_functions/)

### Examples

```
>> AiryAi(1.8)
0.0470362

>> AiryAi(2.5+I)
-0.00191209+I*(-0.0180329)
```

`AiryAi(0)` has a closed form:

```
>> AiryAi(0)
1/(3^(2/3)*Gamma(2/3))
```

The derivative is the `AiryAiPrime` function:

```
>> D(AiryAi(x),x)
AiryAiPrime(x)
```







### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of AiryAi](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BesselFunctions.java#L195) 

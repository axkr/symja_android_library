## EllipticK

```
EllipticK(z)
```

> returns the complete elliptic integral of the first kind. 
   

See
* [Wikipedia - Elliptic integral - Complete elliptic integral of the first kind](https://en.wikipedia.org/wiki/Elliptic_integral#Complete_elliptic_integral_of_the_first_kind)
* [Fungrim - Legendre elliptic integrals](http://fungrim.org/topic/Legendre_elliptic_integrals/)

### Examples

```
>> Table(EllipticK(x+I), {x,-1.0, 1.0, 1/4})
{1.26549+I*0.16224,1.30064+I*0.18478,1.33866+I*0.21305,1.37925+I*0.24904,1.42127+I*0.29538,1.46203+I*0.35524,1.49611+I*0.43136,1.51493+I*0.52354,1.50924+I*0.62515}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of EllipticK](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java#L721) 

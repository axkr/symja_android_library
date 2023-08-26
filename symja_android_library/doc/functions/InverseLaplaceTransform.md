## InverseLaplaceTransform

```
InverseLaplaceTransform(f,s,t)
```

> returns the inverse laplace transform.
 
See:
* [Wikipedia - Laplace transform](https://en.wikipedia.org/wiki/Laplace_transform)

### Examples 
```  
>> InverseLaplaceTransform(3/(s-1)+(2*s)/(s^2+4),s,t)
3*E^t+2*Cos(2*t)
```






### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of InverseLaplaceTransform](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/InverseLaplaceTransform.java#L48) 

* [Rule definitions of InverseLaplaceTransform](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/InverseLaplaceTransformRules.m) 

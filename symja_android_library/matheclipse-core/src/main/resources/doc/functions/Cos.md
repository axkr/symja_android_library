## Cos

```
Cos(expr)
```

> returns the cosine of `expr` (measured in radians). `Cos(expr)` will evaluate automatically in the case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.

See:
* [Wikipedia - Trigonometric functions](https://en.wikipedia.org/wiki/Trigonometric_functions)
* [Wikipedia - Radian](https://en.wikipedia.org/wiki/Radian)
* [Wikipedia - Degree](https://en.wikipedia.org/wiki/Degree_(angle))

### Examples
```
>> Cos(0)
1

>> Cos(3*Pi)
-1    
 
>> Cos(1.5*Pi)   
-1.8369701987210297E-16  
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Cos](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L1293) 

* [Rule definitions of Cos](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/CosRules.m) 

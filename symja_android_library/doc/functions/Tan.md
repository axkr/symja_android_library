## Tan

```
Tan(expr)
```

> returns the tangent of `expr` (measured in radians).
 
`Tan(expr)` will evaluate automatically in the case `expr` is a multiple of `Pi`.

See:
* [Wikipedia - Trigonometric functions](https://en.wikipedia.org/wiki/Trigonometric_functions)
* [Wikipedia - Radian](https://en.wikipedia.org/wiki/Radian)
* [Wikipedia - Degree](https://en.wikipedia.org/wiki/Degree_(angle))

### Examples

```
>> Tan(1/4*Pi)
1
   
>> Tan(0)    
0    
 
>> Tan(Pi / 2)    
ComplexInfinity    
 
>> Tan(0.5 Pi)    
1.633123935319537E16
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Tan](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L3276) 

* [Rule definitions of Tan](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/TanRules.m) 

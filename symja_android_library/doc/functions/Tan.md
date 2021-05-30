## Tan

```
Tan(expr)
```

> returns the tangent of `expr` (measured in radians).
 
`Tan(expr)` will evaluate automatically in the case `expr` is a multiple of `Pi`.

See:
* [Wikipedia - Trigonometric functions](https://en.wikipedia.org/wiki/Trigonometric_functions)

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

### Github

* [Implementation of Tan](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L3123) 

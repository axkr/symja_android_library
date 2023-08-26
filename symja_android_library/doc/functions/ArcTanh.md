## ArcTanh

```
ArcTanh(z)
```

> returns the inverse hyperbolic tangent of `z`.

See:
* [Wikipedia: Inverse hyperbolic function](https://en.wikipedia.org/wiki/Inverse_hyperbolic_function)

### Examples

``` 
>> ArcTanh(0)    
0  

>> ArcTanh(1)    
Infinity    
  
>> ArcTanh(.5 + 2*I)
0.09641562020299621+I*1.1265564408348223  

>> ArcTanh(2+I)    
ArcTanh(2+I)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ArcTanh](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L1165) 

* [Rule definitions of ArcTanh](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/ArcTanhRules.m) 

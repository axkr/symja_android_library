## Log

```
Log(z)
```

> returns the natural logarithm of `z`.

```
Log(base, z)
```

> get the logarithm of `z` for base `base`.

For the natural logarithm functions `Ln(x)` or `Log(x)` the logarithms are taken with the natural base, `E`. 
To get a logarithm of a different base `b`, use `Log(b, x)`, which is essentially a short-hand for `Log(x)/Log(b)`. Other short-hands are `Log10(x)` for `Log(x)/Log(10)` and `Log2(x)` for `Log(x)/Log(2)`.

See
* [Wikipedia - Logarithm](https://en.wikipedia.org/wiki/Logarithm)
* [Fungrim - Natural logarithm](http://fungrim.org/topic/Natural_logarithm/)

### Examples

```
>> Log(E)
1

>> Log({0, 1, E, E * E, E ^ 3, E ^ x})    
{-Infinity,0,1,2,3,Log(E^x)} 
 
>> Log(0.)    
Indeterminate 
 
>> Log(1000) / Log(10)  
3    
 
>> Log(1.4)    
0.3364722366212129    
 
>> Log(Exp(1.4))    
1.3999999999999997   
 
>> Log(-1.4)     
0.3364722366212129+I*3.141592653589793
 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Log](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2288) 

* [Rule definitions of Log](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/LogRules.m) 

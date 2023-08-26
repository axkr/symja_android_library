## Log10

```
Log10(z)
```

> returns the base-`10` logarithm of `z`. `Log10(z)` will be converted to `Log(z)/Log(10)` in symbolic mode.

See
* [Wikipedia - Logarithm](https://en.wikipedia.org/wiki/Logarithm)

### Examples 

```
>> Log10(1000)    
3    
 
>> Log10({2., 5.})     
{0.30102999566398114,0.6989700043360186}
 
>> Log10(E ^ 3)    
3/Log(10)   
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Log10](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2561) 

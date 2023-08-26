## InverseErf

```
InverseErf(z)
```

> returns the inverse error function of `z`.


See
* [Wikipedia - Error_function - Inverse functions](https://en.wikipedia.org/wiki/Error_function#Inverse_functions) 
 
### Examples

`InverseErf(z)` is an odd function:

```  
>> InverseErf /@ {-1, 0, 1}    
{-Infinity, 0, Infinity}     
``` 

'InverseErf($z$)' only returns numeric values for '-1 <= $z$ <= 1':    
``` 
>> InverseErf /@ {0.9, 1.0, 1.1}    
{1.1630871536766743,Infinity,InverseErf(1.1)} 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseErf](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java#L965) 

## InverseErfc

```
InverseErfc(z)
```

> returns the inverse complementary error function of `z`.

See
* [Wikipedia - Error_function - Inverse functions](https://en.wikipedia.org/wiki/Error_function#Inverse_functions) 

### Examples 
```  
>> InverseErfc /@ {0, 1, 2}
{Infinity,0,-Infinity}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseErfc](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java#L1024) 

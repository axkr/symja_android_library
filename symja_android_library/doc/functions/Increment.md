## Increment

```
Increment(x)

x++
```

> increments `x` by `1`, returning the original value of `x`. 

### Examples

```   
>> a = 2;   
>> a++    
2    
 
>> a    
3    
```

Grouping of 'Increment', 'PreIncrement' and 'Plus':
   
``` 
>> ++++a+++++2//Hold//FullForm    
Hold(Plus(PreIncrement(PreIncrement(Increment(Increment(a)))), 2))  
```
    






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Increment](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L2277) 

## BitLength

```
BitLengthi(x)
```

> gives the number of bits needed to represent the integer `x`. The sign of `x` is ignored. 
 
### Examples

```
>> BitLength(1023)    
10  
 
>> BitLength(100)    
7    
 
>> BitLength(-5)    
3    
 
>> BitLength(0)    
0    
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BitLength](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntegerFunctions.java#L171) 

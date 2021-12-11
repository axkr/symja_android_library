## ProductLog

```
ProductLog(z)
```

> returns the value of the Lambert W function at `z`.
 
See
* [Wikipedia - Lambert W function](https://en.wikipedia.org/wiki/Lambert_W_function)
* [Fungrim - Lambert W function](http://fungrim.org/topic/Lambert_W-function/)

### Examples

The defining equation:

```
>> z == ProductLog(z) * E ^ ProductLog(z)    
True    
```

Some special values:  
  
```
>> ProductLog(0)    
0 

>> ProductLog(E)    
1   
```
 

### Github

* [Implementation of ProductLog](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java#L1572) 

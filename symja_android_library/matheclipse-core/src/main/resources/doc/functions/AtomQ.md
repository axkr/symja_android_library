## AtomQ
``` 
AtomQ(x)
```
 
> is true if `x` is an atom (an object such as a number or string, which cannot be divided into subexpressions using 'Part').

### Examples
``` 
>> AtomQ(x)
True
 
>> AtomQ(1.2)
True
 
>> AtomQ(2 + I)
True
 
>> AtomQ(2 / 3)
True
 
>> AtomQ(x + y)
False
``` 

### Github

* [Implementation of AtomQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/expression/BuiltInSymbol.java#L63) 

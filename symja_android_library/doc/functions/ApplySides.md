## ApplySides

```
ApplySides(compare-expr, value) 
```

> divides all elements of the `compare-expr` by `value`. `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal, Less, LessEqual, Greater, GreaterEqual`. 

### Examples

```
>> ApplySides(f, a==a) 
True      
 
>> ApplySides(Log, E^2==b)    
2==Log(b)
```
    






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ApplySides](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SidesFunctions.java#L80) 

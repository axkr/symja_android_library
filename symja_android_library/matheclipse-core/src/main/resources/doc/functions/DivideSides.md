## DivideSides

```
DivideSides(compare-expr, value) 
```

> divides all elements of the `compare-expr` by `value`. `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal, Less, LessEqual, Greater, GreaterEqual`. 

### Examples

```
>> DivideSides(a==a, x) 
True      
 
>> DivideSides(a==b, x)    
a/x==b/x
```
    






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DivideSides](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SidesFunctions.java#L131) 

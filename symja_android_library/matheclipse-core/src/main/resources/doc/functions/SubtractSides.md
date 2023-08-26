## SubtractSides

```
SubtractSides(compare-expr, value) 
```

> subtracts `value` from all elements of the `compare-expr`. `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal, Less, LessEqual, Greater, GreaterEqual`. 

### Examples

```
>> SubtractSides(a==a, x) 
True      
 
>> SubtractSides(a==b, x)    
a-x==b-x
```
    






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SubtractSides](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SidesFunctions.java#L341) 

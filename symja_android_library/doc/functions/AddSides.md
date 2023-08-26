## AddSides

```
AddSides(compare-expr, value) 
```

> add `value` to all elements of the `compare-expr`. `compare-expr` can be `True`, `False` or an comparison expression with head `Equal, Unequal, Less, LessEqual, Greater, GreaterEqual`. 

### Examples

```
>> AddSides(1 < 0, x) 
False      
 
>> AddSides(a==b, x)    
a+x==b+x
```
    






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AddSides](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SidesFunctions.java#L26) 

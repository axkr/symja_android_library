## ArrayDepth
   
```
ArrayDepth(a)
```

> returns the depth of the non-ragged array `a`, defined as `Length(Dimensions(a))`.    
 
### Examples

```
>> ArrayDepth({{a,b},{c,d}})   
2    

>> ArrayDepth(x)
0

>> ArrayDepth(SparseArray({{1, 2, 3} -> a}, {2, 3, 4}))
3
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ArrayDepth](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L771) 

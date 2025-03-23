## VectorLess

```
VectorLess({vector1, vector2}) 
```

> the `VectorLess` function is used to compare two vectors, `vector1` and `vector2` recursively. It returns `True` if each corresponding element of `vector1` is less than the corresponding element of `vector2`, and `False` otherwise.
 
### Examples

```
>> VectorLess({{{0,-1}, {2,-1}}, {{1, {2,7}},{3,4}}}) 
True

>> VectorLess({{{0,2}, {2,-1}}, {{1, {2,7}},{3,4}}}) 
False
```
 

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of VectorLess](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L1223) 

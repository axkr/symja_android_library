## VectorGreaterEqual

```
VectorGreaterEqual({vector1, vector2}) 
```

> the `VectorGreaterEqual` function is used to compare two vectors, `vector1` and `vector2` recursively. It returns `True` if each corresponding element of `vector1` is greater or equal than the corresponding element of `vector2`, and `False` otherwise.
 
### Examples

```
>> VectorGreaterEqual({{{1, {2,7}},{3,4}}, {{0,-1}, {2,-1}}}) 
True

>> VectorGreaterEqual({{{1, {2,7}},{3,4}}, {{0,2}, {2,-1}}})
True
```
 

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of VectorGreaterEqual](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L1214) 

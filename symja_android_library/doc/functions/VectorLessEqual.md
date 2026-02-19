## VectorLessEqual

```
VectorLessEqual({vector1, vector2}) 
```

> the `VectorLessEqual` function is used to compare two vectors, `vector1` and `vector2` recursively. It returns `True` if each corresponding element of `vector1` is less or equal than the corresponding element of `vector2`, and `False` otherwise.
 
### Examples

```
>> VectorLessEqual({{{0,-1}, {2,-1}}, {{1, {2,7}},{3,4}}})
True

VectorLessEqual({{{0,2}, {2,-1}}, {{1, {2,7}},{3,4}}})
True
```
 

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of VectorLessEqual](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L1311) 

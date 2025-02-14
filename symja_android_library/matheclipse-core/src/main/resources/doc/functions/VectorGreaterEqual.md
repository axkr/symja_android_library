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
 
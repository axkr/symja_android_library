## VectorGreater

```
VectorGreater({vector1, vector2}) 
```

> the `VectorGreater` function is used to compare two vectors, `vector1` and `vector2` recursively. It returns `True` if each corresponding element of `vector1` is greater than the corresponding element of `vector2`, and `False` otherwise.
 
### Examples

```
>> VectorGreater({{{1, {2,7}},{3,4}}, {{0,-1}, {2,-1}}}) 
True

>> VectorGreater({{{1, {2,7}},{3,4}}, {{0,2}, {2,-1}}})
False
```
 
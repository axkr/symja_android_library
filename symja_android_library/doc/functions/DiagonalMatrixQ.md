## DiagonalMatrixQ

```
DiagonalMatrixQ(matrix)
```

```
DiagonalMatrixQ(matrix, diagonal)
```

> returns `True` if all elements of the `matrix` are `0` except the elements on the `diagonal`.

### Examples

```
>> DiagonalMatrixQ({{a, 0, 0}, {b, 0, 0}, {0, 0, c}}) 
False

>> DiagonalMatrixQ({{0, a, 0, 0}, {0, 0, b, 0}, {0, 0, 0, c}}, 1) 
True

>> DiagonalMatrixQ({{0, a, 0, 0}, {0, 0, b, 0}, {0, 0, c, 0}}, -1)
False

>> DiagonalMatrixQ({{0, 0, 0, 0}, {a, 0, 0, 0}, {0, b, 0, 0}}, -1)
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DiagonalMatrixQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L343) 

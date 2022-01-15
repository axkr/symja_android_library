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

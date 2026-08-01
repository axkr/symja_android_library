## DiagonalMatrix

```
DiagonalMatrix(list)
```

> gives a matrix with the values in `list` on its diagonal and zeroes elsewhere.

```
DiagonalMatrix(list, k)
```

> gives a matrix with the values in `list` on the `k`-th diagonal and zeroes elsewhere.

```
DiagonalMatrix(list, k, n)
```

> pads with zeroes to create an `n` x `n` matrix.

### Examples

```
>> DiagonalMatrix({1, 2, 3})
{{1, 0, 0}, {0, 2, 0}, {0, 0, 3}}

>> MatrixForm(%)
 1   0   0
 0   2   0
 0   0   3
 
>> DiagonalMatrix({a, b}, 1)
{{0, a, 0}, {0, 0, b}, {0, 0, 0}}

>> DiagonalMatrix({a, b}, -1)
{{0, 0, 0}, {a, 0, 0}, {0, b, 0}}

>> DiagonalMatrix({1, 2, 3}, 0, 4)
{{1, 0, 0, 0}, {0, 2, 0, 0}, {0, 0, 3, 0}, {0, 0, 0, 0}}

>> DiagonalMatrix(a + b)
DiagonalMatrix(a + b)
```






### Implementation status

* &#x2705; - full supported 

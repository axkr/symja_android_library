## DiagonalMatrix

```
DiagonalMatrix(list)
```

> gives a matrix with the values in `list` on its diagonal and zeroes elsewhere.

### Examples

```
>> DiagonalMatrix({1, 2, 3})
{{1, 0, 0}, {0, 2, 0}, {0, 0, 3}}

>> MatrixForm(%)
 1   0   0
 0   2   0
 0   0   3
 
>> DiagonalMatrix(a + b)
DiagonalMatrix(a + b)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DiagonalMatrix](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L1557) 

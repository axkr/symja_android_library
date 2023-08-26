## SquareMatrixQ

```
SquareMatrixQ(m)
```

> returns `True` if `m` is a square matrix.

See
* [Wikipedia - Square matrix](https://en.wikipedia.org/wiki/Square_matrix)
* [Wikipedia - Matrix (mathematics)](https://en.wikipedia.org/wiki/Matrix_(mathematics))

### Examples

```
>> SquareMatrixQ({{1, 3 + 4*I}, {3 - 4*I, 2}})
True

>> SquareMatrixQ({{}})
False
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SquareMatrixQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L1341) 

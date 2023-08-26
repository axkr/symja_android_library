## MatrixPower

```
MatrixPower(matrix, n)
```

> computes the `n`th power of a `matrix`

See
* [Wikipedia - Matrix multiplication - Powers of a matrix](https://en.wikipedia.org/wiki/Matrix_multiplication#Powers_of_a_matrix)

### Examples

```
>> MatrixPower({{1, 2}, {1, 1}}, 10)
{{3363,4756},
 {2378,3363}}

>> MatrixPower({{1, 2}, {2, 5}}, -3)
{{169,-70},
 {-70,29}}
```

Argument {{1, 0}, {0}} at position 1 is not a non-empty rectangular matrix.

```
>> MatrixPower({{1, 0}, {0}}, 2)
MatrixPower({{1, 0}, {0}}, 2)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MatrixPower](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L3762) 

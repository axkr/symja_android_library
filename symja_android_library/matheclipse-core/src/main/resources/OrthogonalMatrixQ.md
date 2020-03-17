## OrthogonalMatrixQ

```
OrthogonalMatrixQ(matrix)
```

> returns `True`, if `matrix` is an orthogonal matrix. `False` otherwise.
 
See
* [Wikipedia - Orthogonal_matrix](https://en.wikipedia.org/wiki/Orthogonal_matrix)

### Examples

Permutation of coordinate axes.

```
>> OrthogonalMatrixQ({{0, 0, 0, 1}, {0, 0, 1, 0}, {1, 0, 0, 0}, {0, 1, 0, 0}})
True
```
 
## Diagonal

```
Diagonal(matrix)
```

> computes the diagonal vector of the `matrix`.
  
```
Diagonal(matrix, n)
```

> computes the diagonal vector of the `n`-th diagonal above or below the main diagonal.

### Examples

```
>> Diagonal({{1,2,3},{4,5,6},{7,8,9}})
{1,5,9}

>> Diagonal({{1,2,3},{4,5,6},{7,8,9}}, -1)
{4,8}
```

  






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Diagonal](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L1467) 

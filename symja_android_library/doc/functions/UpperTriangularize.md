## UpperTriangularize

```
UpperTriangularize(matrix)
```

> create a upper triangular matrix from the given `matrix`.

```
UpperTriangularize(matrix, n)
```

> create a upper triangular matrix from the given `matrix` above the `n`-th subdiagonal.

See
* [Wikipedia - Triangular matrix](https://en.wikipedia.org/wiki/Triangular_matrix)

### Examples
 
```
>> UpperTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}}, 1)
{{0,b,c,d}
 {0,0,f,g}
 {0,0,0,k} 
 {0,0,0,0}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of UpperTriangularize](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L5686) 

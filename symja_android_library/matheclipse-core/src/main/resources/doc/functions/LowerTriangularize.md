## LowerTriangularize

```
LowerTriangularize(matrix)
```

> create a lower triangular matrix from the given `matrix`.

```
LowerTriangularize(matrix, n)
```

> create a lower triangular matrix from the given `matrix` below the `n`-th subdiagonal.

See
* [Wikipedia - Triangular matrix](https://en.wikipedia.org/wiki/Triangular_matrix)

### Examples
 
```
>> LowerTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}}, -1)
{{0,0,0,0} 
 {d,0,0,0} 
 {h,i,0,0}  
 {l,m,n,0}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LowerTriangularize](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L3459) 

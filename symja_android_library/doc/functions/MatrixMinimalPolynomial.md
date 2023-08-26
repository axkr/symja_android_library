## MatrixMinimalPolynomial

```
MatrixMinimalPolynomial(matrix, var)
```

> computes the matrix minimal polynomial of a `matrix` for the variable `var`.

See:  
* [Wikipedia - Minimal polynomial (linear algebra)](https://en.wikipedia.org/wiki/Minimal_polynomial_(linear_algebra))

### Examples
 
```
>> MatrixMinimalPolynomial({{1, -1, -1}, {1, -2, 1}, {0, 1, -3}}, x)
-1+x+4*x^2+x^3
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MatrixMinimalPolynomial](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L3689) 

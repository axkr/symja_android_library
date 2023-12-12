## ConjugateTranspose

```
ConjugateTranspose(matrix)
```

> get the transposed `matrix` with conjugated matrix elements.

```
ConjugateTranspose(tensor, permutation-list)
```

> transposes rows and columns with conjugated matrix elements in the `tensor` according to `permutation-list`. If `tensor` is a `SparseArray` the result will also be a `SparseArray`.

See:  
* [Wikipedia - Transpose](http://en.wikipedia.org/wiki/Transpose) 
* [Wikipedia - Complex conjugation](http://en.wikipedia.org/wiki/Complex_conjugation)
 

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ConjugateTranspose](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L1205) 

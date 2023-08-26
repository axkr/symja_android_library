## Eigensystem

```
Eigensystem(matrix)
```

> return the numerical eigensystem of the `matrix` as a list `{eigenvalues, eigenvectors}`

See
* [Wikipedia - Eigenvalues and Eigenvectors](https://en.wikipedia.org/wiki/Eigenvalues_and_eigenvectors)
* [Youtube - Eigenvectors and eigenvalues | Essence of linear algebra, chapter 14](https://youtu.be/PFDu9oVAE-g)

### Examples

```
>> Eigensystem({{1,0,0},{0,1,0},{0,0,1}})
{{1.0,1.0,1.0},{{1.0,0.0,0.0},{0.0,1.0,0.0},{0.0,0.0,1.0}}}
```

**Note:** Symjas implementation of the `Eigenvectors` function adds zero vectors when the geometric multiplicity of the eigenvalue is smaller than its algebraic multiplicity (hence the regular eigenvector matrix should be non-square).
With these additional null vectors, the `Eigenvectors` result matrix becomes square. 
This happens for example with the following square matrix:

```
>> Eigensystem({{1,0,0},{-2,1,0},{0,0,1}})
{{1.0,1.0,1.0},{{-2.50055*10^-13,1.0,0.0},{0.0,0.0,1.0},{0.0,0.0,0.0}}}
```

Its characteristic polynomial is `(1.0-\[lambda])^3.0`, hence is has one eigen value `\[lambda]==1.0`
with algebraic multiplicity `3`. However, this eigenvalue leads to only two eigenvectors
`v1 = {0.0, 1.0, 0.0}` and `v2 = {0.0, 0.0, 1.0}`, hence its geometric multiplicity is only `2`, not `3`.
So we add a third zero vector `v3 = {0.0, 0.0, 0.0}`.
 
### Related terms 
[Eigenvalues](Eigenvalues.md), [Eigenvectors](Eigenvectors.md), [CharacteristicPolynomial](CharacteristicPolynomial.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Eigensystem](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L1943) 

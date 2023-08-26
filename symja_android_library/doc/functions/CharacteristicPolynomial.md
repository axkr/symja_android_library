## CharacteristicPolynomial

```
CharacteristicPolynomial(matrix, var)
```

> computes the characteristic polynomial of a `matrix` for the variable `var`.

See:  
* [Wikipedia - Characteristic polynomial](https://en.wikipedia.org/wiki/Characteristic_polynomial)
* [Wikipedia - Eigenvalues and Eigenvectors](https://en.wikipedia.org/wiki/Eigenvalues_and_eigenvectors)

### Examples
 
```
>> CharacteristicPolynomial({{1, 2}, {42, 43}}, x)
-41-44*x+x^2
```

### Related terms 
[Eigensystem](Eigensystem.md), [Eigenvalues](Eigenvalues.md), [Eigenvectors](Eigenvectors.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CharacteristicPolynomial](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L985) 

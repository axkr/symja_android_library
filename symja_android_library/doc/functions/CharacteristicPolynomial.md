## CharacteristicPolynomial

```
CharacteristicPolynomial(matrix, var)
```

> computes the characteristic polynomial of a `matrix` for the variable `var`.

See:  
* [Wikipedia - Characteristic polynomial](https://en.wikipedia.org/wiki/Characteristic_polynomial)

### Examples
 
```
>> CharacteristicPolynomial({{1, 2}, {42, 43}}, x)
-41-44*x+x^2
```

### Github

* [Implementation of CharacteristicPolynomial](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L767) 

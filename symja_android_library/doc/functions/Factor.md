## Factor 

```
Factor(expr)
```

> factors the polynomial expression `expr`

```
Factor(expr, GaussianIntegers->True)
```

> for gaussian integers you can set the option `GaussianIntegers->True`

```
Factor(expr, Modulus->p)
```

> to treat integers modulo an integer number `p`, you can set the option `Modulus->p`.

See: 
* [Wikipedia - Factorization of polynomials](https://en.wikipedia.org/wiki/Factorization_of_polynomials)
* [Wikipedia - Quadratic formula](https://en.wikipedia.org/wiki/Quadratic_formula)
* [Wikipedia - Cubic equation](https://en.wikipedia.org/wiki/Cubic_equation)
* [Wikipedia - Quartic function](https://en.wikipedia.org/wiki/Quartic_function)
* [Wikipedia - Gaussian integer](https://en.wikipedia.org/wiki/Gaussian_integer) 

### Examples

```
>> Factor(1+2*x+x^2, x)
(1+x)^2

>> Factor(x^4-1, GaussianIntegers->True)
(x-1)*(x+1)*(x-I)*(x+I)
```

### Github

* [Implementation of Factor](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L2159) 

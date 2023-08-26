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

>> Factor(x^3 + 3*x^2*y + 3*x*y^2 + y^3) 
(x+y)^3 
```

Factor can also be used with equations:

```
>> Factor(x*a == x*b+x*c) 
a*x==(b+c)*x
```

and lists:

```
>> Factor({x + x^2, 2*x + 2*y + 2})  
{x*(1+x),2*(1+x+y)}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Factor](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L2081) 

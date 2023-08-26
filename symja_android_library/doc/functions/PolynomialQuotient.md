## PolynomialQuotient

```
PolynomialQuotient(p, q, x)
```

> returns the polynomial quotient of the polynomials `p` and `q` for the variable `x`.

```
PolynomialQuotient(p, q, x, Modulus -> prime)
```

> returns the polynomial quotient of the polynomials `p` and `q` for the variable `x` modulus the `prime` integer.
  
See
* [Wikipedia: Polynomial long division](https://en.wikipedia.org/wiki/Polynomial_long_division)

### Examples

```
>> PolynomialQuotient(x^2 + 7*x + 6, x^2-5*x-6, x) 
1
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PolynomialQuotient](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L3562) 

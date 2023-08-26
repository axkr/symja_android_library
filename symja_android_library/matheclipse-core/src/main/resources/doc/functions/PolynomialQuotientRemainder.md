## PolynomialQuotientRemainder

```
PolynomialQuotientRemainder(p, q, x)
```

> returns a list with the polynomial quotient and remainder of the polynomials `p` and `q` for the variable `x`.

```
PolynomialQuotientRemainder(p, q, x, Modulus -> prime)
```

> returns list with the polynomial quotient and remainder of the polynomials `p` and `q` for the variable `x` modulus the `prime` integer.
 
See
* [Wikipedia: Polynomial long division](https://en.wikipedia.org/wiki/Polynomial_long_division)

### Examples

```
>> PolynomialQuotientRemainder(x^2 + 7*x + 6, x^2-5*x-6, x) 
{1,12+12*x}
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PolynomialQuotientRemainder](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L3668) 

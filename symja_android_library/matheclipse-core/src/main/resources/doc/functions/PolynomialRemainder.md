## PolynomialQuotient

```
PolynomialQuotient(p, q, x)
```

> returns the polynomial remainder of the polynomials `p` and `q` for the variable `x`.

```
PolynomialQuotient(p, q, x, Modulus -> prime)
```

> returns the polynomial remainder of the polynomials `p` and `q` for the variable `x` modulus the `prime` integer.
 
See
* [Wikipedia: Polynomial long division](https://en.wikipedia.org/wiki/Polynomial_long_division)
 
### Examples

```
>> PolynomialRemainder(x^2 + 7*x + 6, x^2-5*x-6, x)
12+12*x
```
 
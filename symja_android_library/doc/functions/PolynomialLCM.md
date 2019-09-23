## PolynomialLCM

```
PolynomialLCM(p, q)
```

> returns the LCM ('least common multiple') of the polynomials `p` and `q`.

```
PolynomialLCM(p, q, Modulus -> prime)
```

> returns the LCM ('least common multiple') of the polynomials `p` and `q` modulus the `prime` integer.

See
* [Wikipedia: Polynomial greatest common divisor](https://en.wikipedia.org/wiki/Polynomial_greatest_common_divisor)

### Examples

```
>> PolynomialLCM(x^2 + 7*x + 6, x^2-5*x-6) 
-36-36*x+x^2+x^3
```

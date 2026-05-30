## Subresultants

```
Subresultants(polynomial1, polynomial2, var)
```

> computes the subresultants of the polynomials `polynomial1` and `polynomial2` with respect to the variable `var`.

```
Subresultants(polynomial1, polynomial2, var, Modulus->prime)
```

> computes the subresultants  modulo a `prime` number.

See
* [Wikipedia - Polynomial greatest common divisor - Subresultants](https://en.wikipedia.org/wiki/Polynomial_greatest_common_divisor#Subresultants)
* [Wikipedia - Resultant](https://en.wikipedia.org/wiki/Resultant)
 
### Examples

```
>> Subresultants((x-y)^2-2, y^3-5, y)[[1]]
17-60*x+12*x^2-10*x^3-6*x^4+x^6
```
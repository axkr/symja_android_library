## Discriminant

```
Discriminant(poly, var)
```

> computes the discriminant of the polynomial `poly` with respect to the variable `var`.

```
Discriminant(poly, var, Modulus->n)
```

> computes the discriminant with its integer coefficients reduced modulo `n`.

See:  
* [Wikipedia - Discriminant](https://en.wikipedia.org/wiki/Discriminant)
 
### Examples

```
>> Discriminant(a*x^2+b*x+c,x)
b^2-4*a*c

>> Discriminant(a*x^2+b*x+c, x, Modulus->3)
b^2+2*a*c
```






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Discriminant](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PolynomialFunctions.java#L718) 

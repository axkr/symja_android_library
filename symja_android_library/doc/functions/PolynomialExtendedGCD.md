## PolynomialExtendedGCD

```
PolynomialExtendedGCD(p, q, x)
```

> returns the extended GCD ('greatest common divisor') of the univariate polynomials `p` and `q`.

```
PolynomialExtendedGCD(p, q, x, Modulus -> prime)
```

> returns the extended GCD ('greatest common divisor') of the univariate polynomials `p` and `q` modulus the `prime` integer.
 
See
* [Wikipedia: Polynomial extended Euclidean algorithm](https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm#Polynomial_extended_Euclidean_algorithm)

### Examples

```
>> PolynomialExtendedGCD(x^8 + x^4 + x^3 + x + 1, x^6 + x^4 + x + 1, x, Modulus->2)
{1,{1+x^2+x^3+x^4+x^5,x+x^3+x^6+x^7}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PolynomialExtendedGCD](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L3007) 

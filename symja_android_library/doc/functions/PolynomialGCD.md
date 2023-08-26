## PolynomialGCD

```
PolynomialGCD(p, q)
```

> returns the GCD ('greatest common divisor') of the polynomials `p` and `q`.

```
PolynomialGCD(p, q, Modulus -> prime)
```

> returns the GCD ('greatest common divisor') of the polynomials `p` and `q` modulus the `prime` integer.

See
* [Wikipedia: Polynomial greatest common divisor](https://en.wikipedia.org/wiki/Polynomial_greatest_common_divisor)

### Examples

```
>> PolynomialGCD(x^2 + 7*x + 6, x^2-5*x-6) 
1+x
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PolynomialGCD](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L3156) 

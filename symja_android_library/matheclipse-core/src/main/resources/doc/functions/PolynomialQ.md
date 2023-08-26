## PolynomialQ
 
```
PolynomialQ(p, x)
```

> return `True` if `p` is a polynomial for the variable `x`. Return `False` in all other cases.
 
```
PolynomialQ(p, {x, y, ...})
```

> return `True` if `p` is a polynomial for the variables `x, y, ...` defined in the list. Return `False` in all other cases.

See
* [Wikipedia: Polynomial](https://en.wikipedia.org/wiki/Polynomial)

### Examples

```
>> PolynomialQ(x^2 + 7*x + 6) 
True

>> PolynomialQ(Cos(x*y), Cos(x*y))
True

>> PolynomialQ(2*x^3,x^2) 
False
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PolynomialQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L3488) 

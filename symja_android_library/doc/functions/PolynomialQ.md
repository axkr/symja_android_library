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

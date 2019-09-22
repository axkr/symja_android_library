## MonomialList

```
MonomialList(polynomial, list-of-variables)
```

> get the list of monomials of a `polynomial` expression, with respect to the `list-of-variables`. 

See
* [Wikipedia - Monomial](http://en.wikipedia.org/wiki/Monomial)  

### Examples

```
>> MonomialList((x + y)^3) 
{x^3,3*x^2*y,3*x*y^2,y^3}
```
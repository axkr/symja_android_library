## ComplexExpand
 
```
ComplexExpand(expr)
```

> get the expanded `expr`. All variable symbols in `expr` are assumed to be non complex numbers.

See  
* [Wikipedia - List of trigonometric identities](http://en.wikipedia.org/wiki/List_of_trigonometric_identities)
* [Wikipedia - Complex number](https://en.wikipedia.org/wiki/Complex_number) 

### Examples

```
>> ComplexExpand(Sin(x+I*y))
Cosh(y)*Sin(x)+I*Cos(x)*Sinh(y)
```
## ComplexExpand
 
```
ComplexExpand(expr)
```

> expands `expr`. All variable symbols in `expr` are assumed to be non complex numbers.

```
ComplexExpand(expr, {x1, x2, ...})
```
> expands `expr` assuming that variables matching any of the `xi` are complex.

See  
* [Wikipedia - List of trigonometric identities](http://en.wikipedia.org/wiki/List_of_trigonometric_identities)
* [Wikipedia - Complex number](https://en.wikipedia.org/wiki/Complex_number) 

### Examples

Assume that both `x` and `y` are real:

```
>> ComplexExpand(Sin(x+I*y))
Cosh(y)*Sin(x)+I*Cos(x)*Sinh(y)
```

``` 
>> ComplexExpand(3^(I*x))
Cos(1/2*x*Log(9))+I*Sin(1/2*x*Log(9))
```
 






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ComplexExpand](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/ComplexExpand.java#L61) 

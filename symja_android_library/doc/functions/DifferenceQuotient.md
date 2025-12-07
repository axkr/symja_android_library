## DifferenceQuotient 

```
DifferenceQuotient(f,  {var, h})
```

> gives the difference quotient `(f(var+h)-f(var))/h` of the expression `f` with respect to `var` and step-size `h`.

```
DifferenceQuotient(f, {var,n,h})
```

> gives the multiple difference quotient of the expression `f` with respect to `var` and step-size `h`.
 

See:  
* [Wikipedia - Difference quotient](https://en.wikipedia.org/wiki/Difference_quotient)

### Examples

```  
>> DifferenceQuotient(x^2, {x, h}) 
h+2*x

>> DifferenceQuotient(Sin(x), {x, h}) 
(-Sin(x)+Sin(h+x))/h
```
 

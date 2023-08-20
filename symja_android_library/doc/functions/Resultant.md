## Resultant

```
Resultant(polynomial1, polynomial2, var)
```

> computes the resultant of the polynomials `polynomial1` and `polynomial2` with respect to the variable `var`.
  
See
* [Wikipedia - Resultant](https://en.wikipedia.org/wiki/Resultant)
 
### Examples

```
>> Resultant((x-y)^2-2 , y^3-5, y)
17-60*x+12*x^2-10*x^3-6*x^4+x^6
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Resultant](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PolynomialFunctions.java#L954) 

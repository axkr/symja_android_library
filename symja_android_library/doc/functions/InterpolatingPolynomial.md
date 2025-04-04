## InterpolatingPolynomial

```
InterpolatingPolynomial(data-list, symbol)
```

> get the polynomial representation for the given `data-list`.

Newton polynomial interpolation, is the interpolation polynomial for a given set of data points in the Newton form. The Newton polynomial is sometimes called Newton's divided differences interpolation polynomial because the coefficients of the polynomial are calculated using divided differences.

See:  
* [Wikipedia - Newton Polynomial](https://en.wikipedia.org/wiki/Newton_polynomial) 

### Examples

```
>> InterpolatingPolynomial({{1,7},{3,11},{5,27}},x)
(3/2*x-5/2)*(x-1)+7
```
 
### Related terms 
[InterpolatingFunction](InterpolatingFunction.md) 






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of InterpolatingPolynomial](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/InterpolatingPolynomial.java#L54) 

## SeriesData

```
SeriesData(x, x0, {coeff0, coeff1, coeff2,...}, nMin, nMax, denominator})
```

> internal structure of a power series at the point `x = x0` the `coeff_i` are coefficients of the power series.

See:
* [Wikipedia - Taylor series](https://en.wikipedia.org/wiki/Taylor_series)
* [Wikipedia - Big O notation](https://en.wikipedia.org/wiki/Big_O_notation)
* [Wikipedia - Formal power series](https://en.wikipedia.org/wiki/Formal_power_series)

### Examples

```
>> SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2) 
Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)
```

### Related terms
[ComposeSeries](ComposeSeries.md), [InverseSeries](InverseSeries.md), [SeriesCoefficient](SeriesCoefficient.md), [Series](Series.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SeriesData](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SeriesFunctions.java#L1591) 

## CauchyDistribution

```
CauchyDistribution(a,b)
```

> returns the Cauchy distribution.

See:  
* [Wikipedia - Cauchy distribution](https://en.wikipedia.org/wiki/Cauchy_distribution)

### Examples

The probability density function of the Cauchy distribution is

```
>> PDF(CauchyDistribution(a, b), x)
1/(b*Pi*(1+(-a+x)^2/b^2))
```
 

### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CauchyDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L1683) 

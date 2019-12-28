## WeibullDistribution

```
WeibullDistribution(a, b)
```

> returns a Weibull distribution.
    
See
* [Wikipedia - Weibull distribution](https://en.wikipedia.org/wiki/Weibull_distribution)
 
 
### Examples

The variance of the Weibull distribution is

```
>> Variance(WeibullDistribution(n, m)) 
m^2*(-Gamma(1+1/n)^2+Gamma(1+2/n))
```

### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 
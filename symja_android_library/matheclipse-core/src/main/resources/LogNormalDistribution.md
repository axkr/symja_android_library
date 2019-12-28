## LogNormalDistribution

```
LogNormalDistribution(m, s)
```

> returns a log-normal distribution.
    
See
* [Wikipedia - Log-normal distribution](https://en.wikipedia.org/wiki/Log-normal_distribution)
 
### Examples

The variance of the log-normal distribution is

```
>> Variance(LogNormalDistribution(m,s)) 
(-1+E^s^2)*E^(2*m+s^2)
```

### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 
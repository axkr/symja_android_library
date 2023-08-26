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






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LogNormalDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L4335) 

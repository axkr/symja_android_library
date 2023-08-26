## DiscreteUniformDistribution

```
DiscreteUniformDistribution({min, max})
```

> returns a discrete uniform distribution.

See:  
* [Wikipedia - Discrete uniform distribution](https://en.wikipedia.org/wiki/Discrete_uniform_distribution)

### Examples

The variance of the discrete uniform distribution is

```
>> Variance(DiscreteUniformDistribution({l, r}))
1/12*(-1+(1-l+r)^2)
```

### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DiscreteUniformDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L3458) 

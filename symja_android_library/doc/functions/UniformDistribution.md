## UniformDistribution

```
UniformDistribution({min, max})
```

> returns a uniform distribution.

```
UniformDistribution( )
```

> returns a uniform distribution with `min = 0` and `max = 1`.

See 
* [Wikipedia - Uniform distribution (continous)](https://en.wikipedia.org/wiki/Uniform_distribution_(continuous))
 
### Examples

The variance of the uniform distribution is

```
>> Variance(DiscreteUniformDistribution({l, r})) 
1/12*(-1+(1-l+r)^2)
```


### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of UniformDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L7090) 

## HypergeometricDistribution

```
HypergeometricDistribution(n, s, t)
```

> returns a hypergeometric distribution.
    
See:  
* [Wikipedia - Hypergeometric distribution](https://en.wikipedia.org/wiki/Hypergeometric_distribution)
 
 
### Examples

The variance of the hypergeometric distribution is

```
>> Variance(HypergeometricDistribution(n, ns, nt))
(n*ns*(1-ns/nt)*(-n+nt))/((-1+nt)*nt)
```

### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of HypergeometricDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L3131) 

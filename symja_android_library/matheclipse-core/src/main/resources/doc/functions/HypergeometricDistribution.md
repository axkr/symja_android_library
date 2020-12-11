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
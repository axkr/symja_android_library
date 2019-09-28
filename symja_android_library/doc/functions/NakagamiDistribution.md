## NakagamiDistribution

```
NakagamiDistribution(m, o)
```

> returns a Nakagami distribution.
    
See 
* [Wikipedia - Nakagami distribution](https://en.wikipedia.org/wiki/Nakagami_distribution)
 
 
### Examples

The variance of the Nakagami distribution is

```
>> Variance(NakagamiDistribution(n, m)) 
m+(-m*Pochhammer(n,1/2)^2)/n
```

### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Mean.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 
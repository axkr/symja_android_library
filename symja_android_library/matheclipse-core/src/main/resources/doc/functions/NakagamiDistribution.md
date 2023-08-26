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
[CDF](CDF.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NakagamiDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L4905) 

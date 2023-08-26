## ErlangDistribution

```
ErlangDistribution({k, lambda})
```

> returns a Erlang distribution.
    
See:  
* [Wikipedia - Erlang distribution](https://en.wikipedia.org/wiki/Erlang_distribution)
 
### Examples

The variance of the Erlang distribution is

```
>> Variance(ErlangDistribution(n, m)
n/m^2
```

### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ErlangDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L3636) 

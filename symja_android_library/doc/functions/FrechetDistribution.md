## FrechetDistribution

```
FrechetDistribution(a,b)
```

> returns a Frechet distribution.
    
See:  
* [Wikipedia - Frechet distribution](https://en.wikipedia.org/wiki/Fr%C3%A9chet_distribution)
 
### Examples

The variance of the Frechet distribution is

```
>> FrechetDistribution(n, m)) 
Piecewise({{m^2*(Gamma(1-2/n)-Gamma(1-1/n)^2),n>2}},Infinity)
```

### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FrechetDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L2116) 

## ParetoDistribution

```
ParetoDistribution(k,a)
```

```
ParetoDistribution(k,a,m)
```

```
ParetoDistribution(k,a,g,m)
```

> returns a Pareto distribution.

See 
* [Wikipedia - Pareto distribution](https://en.wikipedia.org/wiki/Pareto_distribution)
 
### Examples

The variance of the Pareto distribution is

```
>> Variance(ParetoDistribution(k,a))
Piecewise({{(a*k^2)/((1-a)^2*(-2+a)),a>2}},Indeterminate)
```


### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 








### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ParetoDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L5636) 

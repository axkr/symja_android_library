## InterquartileRange

```
InterquartileRange(list)
```

> returns the interquartile range (IQR), which is between upper and lower quartiles, IQR = Q3 −  Q1. 

```
InterquartileRange(distribution)
```

> returns the interquartile range (IQR) of the `distribution`.
 

See
* [Wikipedia - Interquartile range](https://en.wikipedia.org/wiki/Interquartile_range)

`InterquartileRange` can be applied to the following distributions:

> [BernoulliDistribution](BernoulliDistribution.md), [CauchyDistribution](CauchyDistribution.md), [ErlangDistribution](ErlangDistribution.md), [ExponentialDistribution](ExponentialDistribution.md), [FrechetDistribution](FrechetDistribution.md), 
[GammaDistribution](GammaDistribution.md), [GumbelDistribution](GumbelDistribution.md), [LogNormalDistribution](LogNormalDistribution.md), [NakagamiDistribution](NakagamiDistribution.md), [NormalDistribution](NormalDistribution.md),  [StudentTDistribution](StudentTDistribution.md), [WeibullDistribution](WeibullDistribution.md) 


### Examples

```
>> InterquartileRange({7,7,31,31,47,75,87,115,116,119,119,155,177})
88
```

### Related terms 
[FiveNum](FiveNum.md), [Quantile](Quantile.md), [Quartiles](Quartiles.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InterquartileRange](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L4044) 

* [Rule definitions of InterquartileRange](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/InterquartileRangeRules.m) 

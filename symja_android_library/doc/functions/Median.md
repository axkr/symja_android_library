## Median

```
Median(list)
```

> returns the median of `list`. 
  
See:
* [Wikipedia - Median](https://en.wikipedia.org/wiki/Median)

`Median` can be applied to the following distributions:

> [BernoulliDistribution](BernoulliDistribution.md), [BinomialDistribution](BinomialDistribution.md), [DiscreteUniformDistribution](DiscreteUniformDistribution.md),  [ErlangDistribution](ErlangDistribution.md), [ExponentialDistribution](ExponentialDistribution.md), [FrechetDistribution](FrechetDistribution.md), 
[GammaDistribution](GammaDistribution.md), [GeometricDistribution](GeometricDistribution.md), [GumbelDistribution](GumbelDistribution.md), [HypergeometricDistribution](HypergeometricDistribution.md), [LogNormalDistribution](LogNormalDistribution.md), [NakagamiDistribution](NakagamiDistribution.md), [NormalDistribution](NormalDistribution.md), [StudentTDistribution](StudentTDistribution.md), [WeibullDistribution](WeibullDistribution.md) 

### Examples

``` 
>> Median({26, 64, 36})
36
```

For lists with an even number of elements, Median returns the mean of the two middle values:

```
>> Median({-11, 38, 501, 1183})
539/2
```

Passing a matrix returns the medians of the respective columns:

```
>> Median({{100, 1, 10, 50}, {-1, 1, -2, 2}})
{99/2,1,4,26}

>> Median(LogNormalDistribution(m,s))
E^m
```

### Related terms 
[FiveNum](FiveNum.md), [Quantile](Quantile.md), [Quartiles](Quartiles.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Median](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L4751) 

## Quantile

```
Quantile(list, q)
```

> returns the `q`-Quantile of `list`. 

```
Quantile(list, {q1, q2, ...})
```

> returns a list of the `q`-Quantiles of `list`. 

```
Quantile(list, q, {{a,b},{c,d}})
```

> returns the `q`-Quantile of `list` with the quantile definition `{{a,b},{c,d}}`. The default parameters for the quantile definition are `{{0,0},{1,0}}`. 


Common choices of parameters `{{a,b},{c,d}}` include:
* `{{0, 0}, {1, 0}}` - inverse empirical CDF (default)
* `{{0, 0}, {0, 1}}` - linear interpolation (California method)
   
   
In statistics and probability, quantiles are cut points dividing the range of a probability distribution into continuous intervals with equal probabilities, or dividing the observations in a sample in the same way. Quantile is also known as value at risk (VaR) or fractile.
    

See
* [Wikipedia - Quantile](https://en.wikipedia.org/wiki/Quantile)

`Quantile` can be applied to the following distributions:

> [BernoulliDistribution](BernoulliDistribution.md), [ErlangDistribution](ErlangDistribution.md), [ExponentialDistribution](ExponentialDistribution.md), [FrechetDistribution](FrechetDistribution.md), 
[GammaDistribution](GammaDistribution.md), [GumbelDistribution](GumbelDistribution.md), [LogNormalDistribution](LogNormalDistribution.md), [NakagamiDistribution](NakagamiDistribution.md), [NormalDistribution](NormalDistribution.md),  [StudentTDistribution](StudentTDistribution.md), [WeibullDistribution](WeibullDistribution.md) 


### Examples

```
>> Quantile({1,2}, 0.5)
1

>> Quantile(NormalDistribution(m, s), q) 
ConditionalExpression(m-Sqrt(2)*s*InverseErfc(2*q),0<=q<=1)

>> Quantile(Range(11), 1/3)
4

>> Quantile(Range(17), 1/4)
5

>> Quantile({1, 2, 3, 4, 5, 6, 7}, {1/4, 3/4})
{2,6}
```

### Related terms 
[FiveNum](FiveNum.md), [Quartiles](Quartiles.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Quantile](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L6194) 

* [Rule definitions of Quantile](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/QuantileRules.m) 

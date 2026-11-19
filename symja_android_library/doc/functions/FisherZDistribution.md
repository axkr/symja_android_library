## FisherZDistribution

```
FisherZDistribution(n, m)
```

> returns the Fisher z-distribution with `n` numerator and `m` denominator degrees of freedom.

The Fisher z-distribution is the distribution of `Log(u)/2` for `u` distributed as `FRatioDistribution(n, m)`. It is defined for all real numbers. The degrees of freedom `n` and `m` can be any positive real numbers.

The probability density is `2*m^(m/2)*n^(n/2)*E^(n*x)*(m + n*E^(2*x))^(-(m + n)/2)/Beta(n/2, m/2)` and the cumulative distribution function is `BetaRegularized((n*E^(2*x))/(m + n*E^(2*x)), n/2, m/2)`. The moments are written with `PolyGamma`, because `Log(u)` of the `FRatioDistribution` is the difference of the logarithms of two gamma distributed variables.

See:
* [Wikipedia - Fisher's z-distribution](https://en.wikipedia.org/wiki/Fisher%27s_z-distribution)

### Examples

```
>> PDF(FisherZDistribution(n, m), x)
(2*E^(n*x)*m^(m/2)*n^(n/2)*(m+E^(2*x)*n)^(1/2*(-m-n)))/Beta(n/2,m/2)

>> CDF(FisherZDistribution(n, m), x)
BetaRegularized((E^(2*x)*n)/(m+E^(2*x)*n),n/2,m/2)

>> CDF(FisherZDistribution(4, 2), x)
(16*E^(4*x))/(2+4*E^(2*x))^2

>> Mean(FisherZDistribution(n, m))
1/2*(Log(m/n)-PolyGamma(0,m/2)+PolyGamma(0,n/2))

>> Variance(FisherZDistribution(n, m))
1/4*(PolyGamma(1,m/2)+PolyGamma(1,n/2))

>> Skewness(FisherZDistribution(n, m))
(-PolyGamma(2,m/2)+PolyGamma(2,n/2))/(PolyGamma(1,m/2)+PolyGamma(1,n/2))^(3/2)

>> Kurtosis(FisherZDistribution(n, m))
3+(PolyGamma(3,m/2)+PolyGamma(3,n/2))/(PolyGamma(1,m/2)+PolyGamma(1,n/2))^2

>> Mean(FisherZDistribution(4, 2))
1/2*(1-Log(2))

>> MomentGeneratingFunction(FisherZDistribution(n, m), t)
((m/n)^(t/2)*Gamma(1/2*(m-t))*Gamma(1/2*(n+t)))/(Gamma(m/2)*Gamma(n/2))
```

Machine numbers:

```
>> PDF(FisherZDistribution(3, 5), {-1.0, 0.0, 0.3})
{0.172472,0.722349,0.606443}

>> CDF(FisherZDistribution(3, 5), {-1.0, 0.0, 0.3})
{0.0652221,0.535145,0.739932}

>> Quantile(FisherZDistribution(4, 2), {0.25, 0.5, 0.75})
{-0.346574,0.0941132,0.586558}
```

The survival function keeps the precision of a small upper tail, where `1 - CDF(...)` gives `0.0`:

```
>> SurvivalFunction(FisherZDistribution(4, 2), 20.0)
4.24835*10^-18
```

### Related terms

[CDF](CDF.md), [Kurtosis](Kurtosis.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [RandomVariate](RandomVariate.md), [Skewness](Skewness.md), [StandardDeviation](StandardDeviation.md), [SurvivalFunction](SurvivalFunction.md), [Variance](Variance.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FisherZDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsContinousDistribution.java)

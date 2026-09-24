## SkewNormalDistribution

```
SkewNormalDistribution(mu, sigma, alpha)
```

> returns the skew-normal distribution with location parameter `mu`, scale parameter `sigma` and shape parameter `alpha`.

```
SkewNormalDistribution(alpha)
```

> is equivalent to `SkewNormalDistribution(0, 1, alpha)`.

The scale parameter `sigma` has to be positive. `SkewNormalDistribution(mu, sigma, 0)` is the `NormalDistribution(mu, sigma)`.

See
* [Wikipedia - Skew normal distribution](https://en.wikipedia.org/wiki/Skew_normal_distribution)

### Examples

```
>> PDF(SkewNormalDistribution(m, s, a), x)
Erfc((a*(m-x))/(Sqrt(2)*s))/(E^((-m+x)^2/(2*s^2))*Sqrt(2*Pi)*s)

>> CDF(SkewNormalDistribution(m, s, a), x)
Erfc((m-x)/(Sqrt(2)*s))/2-2*OwenT((-m+x)/s,a)

>> Mean(SkewNormalDistribution(m, s, a))
m+(a*Sqrt(2/Pi)*s)/Sqrt(1+a^2)

>> Variance(SkewNormalDistribution(m, s, a))
(1+(-2*a^2)/((1+a^2)*Pi))*s^2

>> Skewness(SkewNormalDistribution(m, s, a))
(Sqrt(2)*a^3*(4-Pi))/(a^2*(-2+Pi)+Pi)^(3/2)

>> Kurtosis(SkewNormalDistribution(m, s, a))
3+(8*a^4*(-3+Pi))/(a^2*(-2+Pi)+Pi)^2

>> CDF(SkewNormalDistribution(0, 1, 2), 0.5)
0.408301
```

### Related terms

[CDF](CDF.md), [Kurtosis](Kurtosis.md), [Mean](Mean.md), [NormalDistribution](NormalDistribution.md), [OwenT](OwenT.md), [PDF](PDF.md), [RandomVariate](RandomVariate.md), [Skewness](Skewness.md), [Variance](Variance.md)

### Implementation status

* &#x1F9EA; - experimental

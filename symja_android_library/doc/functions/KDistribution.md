## KDistribution

```
KDistribution(nu, w)
```

> returns the K distribution with the shape parameters `nu` and `w`.

The K distribution is the distribution of `Sqrt(s*u)` for `s` distributed as `GammaDistribution(nu, w/nu)` and `u` distributed as `ExponentialDistribution(1)`. So it is a `RayleighDistribution` whose squared scale is gamma distributed. It is used for the amplitude of radar clutter and of scattered laser light. The parameter `w` is the mean square `E[x^2]`. Both parameters can be any positive real numbers. The distribution is defined for `x > 0`.

The probability density is `(4*x^nu*(nu/w)^((1 + nu)/2)*BesselK(nu - 1, 2*x*Sqrt(nu/w)))/Gamma(nu)` and the survival function is `(2*(x*Sqrt(nu/w))^nu*BesselK(nu, 2*x*Sqrt(nu/w)))/Gamma(nu)`. The raw moments are `E[x^k] == (w/nu)^(k/2)*Pochhammer(nu, k/2)*Gamma(1 + k/2)`. `Quantile` and `Median` have no closed form.

See:
* [Wikipedia - K-distribution](https://en.wikipedia.org/wiki/K-distribution)

### Examples

```
>> PDF(KDistribution(nu, w), x)
Piecewise({{(4*(nu/w)^(1/2*(1+nu))*x^nu*BesselK(-1+nu,2*Sqrt(nu/w)*x))/Gamma(nu),x>0}},0)

>> CDF(KDistribution(2, 3), x)
Piecewise({{1-4/3*x^2*BesselK(2,2*Sqrt(2/3)*x),x>0}},0)

>> Mean(KDistribution(nu, w))
(Sqrt(Pi)*Sqrt(w)*Pochhammer(nu,1/2))/(2*Sqrt(nu))

>> Variance(KDistribution(nu, w))
w*(1+(-Pi*Pochhammer(nu,1/2)^2)/(4*nu))

>> Moment(KDistribution(nu, w), 3)
3/4*Sqrt(Pi)*(w/nu)^(3/2)*Pochhammer(nu,3/2)

>> Mean(KDistribution(2, 3))
3/8*Sqrt(3/2)*Pi
```

Machine numbers:

```
>> PDF(KDistribution(2.5, 3), {0.5, 1.0, 2.0})
{0.426539,0.505802,0.268262}

>> CDF(KDistribution(2.5, 3), {0.5, 1.0, 2.0})
{0.120739,0.365781,0.763937}

>> N({Skewness(KDistribution(2.5, 3)), Kurtosis(KDistribution(2.5, 3))})
{1.20687,5.14793}

>> SurvivalFunction(KDistribution(2, 3), 40.0)
1.45872*10^-26
```

### Related terms

[BesselK](BesselK.md), [CDF](CDF.md), [GammaDistribution](GammaDistribution.md), [Kurtosis](Kurtosis.md), [Mean](Mean.md), [PDF](PDF.md), [RandomVariate](RandomVariate.md), [Skewness](Skewness.md), [StandardDeviation](StandardDeviation.md), [SurvivalFunction](SurvivalFunction.md), [Variance](Variance.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of KDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsContinousDistribution.java)

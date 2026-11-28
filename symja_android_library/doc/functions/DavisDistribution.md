## DavisDistribution

```
DavisDistribution(b, n, mu)
```

> returns the Davis distribution with the scale `b`, the shape `n` and the location `mu`.

`b` is positive and `n` is greater than `1`. `x` is Davis distributed if `t = b/(x - mu)` has the density `t^(n-1)/((E^t - 1)*Gamma(n)*Zeta(n))` of the shape of Planck's law, so the density of `x` is

```
b^n*(x - mu)^(-1-n)/((E^(b/(x - mu)) - 1)*Gamma(n)*Zeta(n))
```

for `x > mu`. The moments of the order `k` are finite for `n > k + 1`, otherwise they are `Infinity`. `CDF` and `SurvivalFunction` have no closed form and are evaluated for numerical arguments.

See
* [Wikipedia - Davis distribution](https://en.wikipedia.org/wiki/Davis_distribution)

### Examples

```
>> PDF(DavisDistribution(b, n, mu), x)
Piecewise({{b^n/(E^(b/(-mu+x))*(1-1/E^(b/(-mu+x)))*(-mu+x)^(1+n)*Gamma(n)*Zeta(n)),x>mu}},0)

>> Mean(DavisDistribution(b, n, mu))
Piecewise({{mu+(b*Zeta(-1+n))/((-1+n)*Zeta(n)),n>2}},Infinity)

>> Mean(DavisDistribution(2, 4, 1))
1+(60*Zeta(3))/Pi^4

>> Variance(DavisDistribution(1, 3, 0))
Infinity

>> N({Skewness(DavisDistribution(2, 6, 1)), Kurtosis(DavisDistribution(2, 7, 1))})
{3.26859,19.75976}

>> CDF(DavisDistribution(2, 3.5, 1), {1.2, 1.5, 2.0, 3.0})
{0.00494331,0.297176,0.720262,0.926439}

>> SurvivalFunction(DavisDistribution(2, 3.5, 1), 100.0)
6.15195*10^-6
```

### Related terms

[CDF](CDF.md), [Mean](Mean.md), [PDF](PDF.md), [RandomVariate](RandomVariate.md), [SurvivalFunction](SurvivalFunction.md), [Variance](Variance.md), [Zeta](Zeta.md)

### Implementation status

* &#x2611; - partially implemented

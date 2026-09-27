## InverseGammaDistribution

```
InverseGammaDistribution(a,b)
```

> returns an inverse gamma distribution with shape `a` and scale `b`.

```
InverseGammaDistribution(a,b,g,m)
```

> returns a generalized inverse gamma distribution with shapes `a` and `g`, scale `b` and location `m`.
    
See:  
* [Wikipedia - Inverse-gamma distribution](https://en.wikipedia.org/wiki/Inverse-gamma_distribution)

 
### Examples

```
>> PDF(InverseGammaDistribution(a, b), x)
Piecewise({{(b/x)^a/(E^(b/x)*x*Gamma(a)),x>0}},0)

>> CDF(InverseGammaDistribution(a, b), x)
Piecewise({{GammaRegularized(a,b/x),x>0}},0)

>> Mean(InverseGammaDistribution(n, m))
Piecewise({{m/(-1+n),n>1}},Indeterminate)

>> Variance(InverseGammaDistribution(n, m))
Piecewise({{m^2/((1-n)^2*(-2+n)),n>2}},Indeterminate)

>> Kurtosis(InverseGammaDistribution(a, b))
Piecewise({{3+(-66+30*a)/((-4+a)*(-3+a)),a>4}},Indeterminate)

>> InverseCDF(InverseGammaDistribution(a, b), q)
ConditionalExpression(Piecewise({{b/InverseGammaRegularized(a,q),0<q<1},{0,q<=0}},Infinity),0<=q<=1)

>> RandomVariate(InverseGammaDistribution(2, 3), 3)
{1.30613,0.87718,4.04451}
```

### Related terms 
[CDF](CDF.md), [GammaDistribution](GammaDistribution.md), [Mean](Mean.md), [Median](Median.md), [InverseCDF](InverseCDF.md), [Kurtosis](Kurtosis.md), [PDF](PDF.md), [Quantile](Quantile.md), [RandomVariate](RandomVariate.md), [Skewness](Skewness.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 

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

>> RandomVariate(InverseGammaDistribution(2, 3), 3)
{1.30613,0.87718,4.04451}
```

### Related terms 
[CDF](CDF.md), [GammaDistribution](GammaDistribution.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [RandomVariate](RandomVariate.md) 

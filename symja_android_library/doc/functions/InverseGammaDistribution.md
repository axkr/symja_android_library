## InverseGammaDistribution

```
InverseGammaDistribution(a,b)
```

> returns a inverse gamma distribution.
    
See:  
* [Wikipedia - Inverse-gamma distribution](https://en.wikipedia.org/wiki/Inverse-gamma_distribution)

 
### Examples

The variance of the inverse gamma distribution is

```
>> Variance(InverseGammaDistribution(n, m)) 
Piecewise({{m^2/((1 - n)^2*(-2 + n)),n>2}},Indeterminate)
```

### Related terms 
[CDF](CDF.md), [GammaDistribution](GammaDistribution.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 

## FrechetDistribution

```
FrechetDistribution(a,b)
```

> returns a Frechet distribution.
    
See:  
* [Wikipedia - Frechet distribution](https://en.wikipedia.org/wiki/Fr%C3%A9chet_distribution)
 
### Examples

The variance of the Frechet distribution is

```
>> FrechetDistribution(n, m)) 
Piecewise({{m^2*(Gamma(1-2/n)-Gamma(1-1/n)^2),n>2}},Infinity)
```

### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Mean.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 
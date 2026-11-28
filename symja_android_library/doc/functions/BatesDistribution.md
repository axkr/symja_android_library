## BatesDistribution

```
BatesDistribution(n)
```

> returns the Bates distribution of the mean of `n` independent variables, which are uniformly distributed on the interval `[0, 1]`.

```
BatesDistribution(n, {min, max})
```

> returns the Bates distribution of the mean of `n` independent variables, which are uniformly distributed on the interval `[min, max]`.

`n` is a positive integer. The density and the distribution function are piecewise polynomials of degree `n-1` and `n` on the `n` subintervals of length `(max-min)/n`. They are the ones of the `UniformSumDistribution(n)` of the sum of the `n` variables, rescaled. `RandomVariate` returns the means of `n` uniformly distributed random numbers.

See
* [Wikipedia - Bates distribution](https://en.wikipedia.org/wiki/Bates_distribution)

### Examples

```
>> PDF(BatesDistribution(2), x)
Piecewise({{0,x<0},{4*x,0<=x<1/2},{2*(2-2*x),1/2<=x<=1}},0)

>> CDF(BatesDistribution(3), x)
Piecewise({{0,x<0},{9/2*x^3,0<=x<1/3},{1/6*(3-27*x+81*x^2-54*x^3),1/3<=x<2/3},{1+1/6*(-27+81*x-81*x^2+27*x^3),2/3<=x<=1}},1)

>> CDF(BatesDistribution(2, {a, b}), x)
Piecewise({{0,x<a},{(2*(-a+x)^2)/(-a+b)^2,a<=x<a+1/2*(-a+b)},{1-(2+(-2*(-a+x))/(-a+b))^2/2,a+1/2*(-a+b)<=x<=b}},1)

>> PDF(BatesDistribution(4, {2, 6}), {3.0, 4.0, 5.5})
{0.166667,0.666667,0.0208333}
```

The mean, the variance and the kurtosis:

```
>> Mean(BatesDistribution(n, {a, b}))
1/2*(a+b)

>> Variance(BatesDistribution(n, {a, b}))
(-a+b)^2/(12*n)

>> Kurtosis(BatesDistribution(n))
3-6/(5*n)
```

### Related terms

[CDF](CDF.md), [Kurtosis](Kurtosis.md), [Mean](Mean.md), [PDF](PDF.md), [RandomVariate](RandomVariate.md), [UniformDistribution](UniformDistribution.md), [Variance](Variance.md)

### Implementation status

* &#x2611; - partially implemented

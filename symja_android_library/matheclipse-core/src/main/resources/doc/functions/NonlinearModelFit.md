## NonlinearModelFit

```
NonlinearModelFit(data, model, {a, b, ...}, x)
```

> fit `model`, which need not be linear in its parameters `a, b, ...`, to `data` and return a
> `FittedModel` that can be asked about the fit.

`data` is a list of `{x, y}` pairs, or a list of values taken at `x = 1, 2, 3, ...`. A parameter can
be given a starting value as `{a, a0}`; the default is `1.0`. The fit is the one
[FindFit](FindFit.md) makes, by Levenberg-Marquardt, and the model has one independent variable.

See:
* [Wikipedia - Nonlinear regression](https://en.wikipedia.org/wiki/Nonlinear_regression)

### Examples

```
>> nlm = NonlinearModelFit({{1,2.1},{2,3.9},{3,6.2},{4,7.8}}, a*x+b, {a,b}, x)
FittedModel[0.15+1.94*x]

>> nlm("BestFitParameters")
{a->1.94,b->0.15}

>> nlm("RSquared")
0.995661

>> nlm("AdjustedRSquared")
0.991323

>> nlm("FitResiduals")
{0.01,-0.13,0.23,-0.11}

>> nlm("PredictedResponse")
{2.09,4.03,5.97,7.91}

>> nlm(2.5)
5.0
```

`RSquared` is measured about the mean of the data, as for a linear fit, but `AdjustedRSquared`
scales by `n/(n-p)` rather than `(n-1)/(n-p)`: a nonlinear model has no constant term it can be
assumed to carry. Both follow the reference implementation.

`nlm("Properties")` lists every property; a name that is not one prints `FittedModel::elmntavs`
with the nearest name there is.

### Related terms
[FindFit](FindFit.md), [Fit](Fit.md), [LinearModelFit](LinearModelFit.md)

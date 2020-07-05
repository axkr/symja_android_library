## ComposeSeries

```
ComposeSeries( series1, series2 )
```

> substitute `series2` into `series1`

```
ComposeSeries( series1, series2, series3 )
```

> return multiple series composed.

See:
* [Wikipedia - Taylor series](https://en.wikipedia.org/wiki/Taylor_series)
* [Wikipedia - Big O notation](https://en.wikipedia.org/wiki/Big_O_notation)
* [Wikipedia - Formal power series](https://en.wikipedia.org/wiki/Formal_power_series)

### Examples

```
>> ComposeSeries(SeriesData(x, 0, {1, 3}, 2, 4, 1), SeriesData(x, 0, {1, 1,0,0}, 0, 4, 1) - 1)
x^2+3*x^3+O(x)^4
```
   
### Related terms
[Series](Series.md), [InverseSeries](InverseSeries.md), [SeriesCoefficient](SeriesCoefficient.md), [SeriesData](SeriesData.md)
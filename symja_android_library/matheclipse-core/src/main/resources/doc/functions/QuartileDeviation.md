## QuartileDeviation

```
QuartileDeviation(list)
```

> returns the quartile deviation or semi-interquartile range of `list`, which is half the interquartile range `(Q3 - Q1)/2`.

```
QuartileDeviation(list, {{a,b},{c,d}})
```

> uses the quantile parameters `{{a,b},{c,d}}` for the quartiles.

```
QuartileDeviation(matrix)
```

> returns the quartile deviation of each column of `matrix`.

```
QuartileDeviation(distribution)
```

> returns the quartile deviation of the `distribution`.

See
* [Wikipedia - Interquartile range](https://en.wikipedia.org/wiki/Interquartile_range)

### Examples

```
>> QuartileDeviation({7,7,31,31,47,75,87,115,116,119,119,155,177})
44

>> QuartileDeviation({1,2,3,4,5,6,7,8}, {{0,0},{1,0}})
2

>> QuartileDeviation({{1,2},{3,4},{5,10},{7,100}})
{2,26}

>> QuartileDeviation(ExponentialDistribution(l))
Log(3)/(2*l)
```

### Related terms

[InterquartileRange](InterquartileRange.md), [Quantile](Quantile.md), [QuartileSkewness](QuartileSkewness.md), [Quartiles](Quartiles.md)

### Implementation status

* &#x2705; - full supported

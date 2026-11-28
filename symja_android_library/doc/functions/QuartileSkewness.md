## QuartileSkewness

```
QuartileSkewness(list)
```

> returns the quartile skewness coefficient (Bowley skewness) of `list`, which is `(Q1 - 2*Q2 + Q3)/(Q3 - Q1)` for the quartiles `{Q1, Q2, Q3}` of `list`. Its value lies between `-1` and `1`; it is `0` for data which is symmetric about the median.

```
QuartileSkewness(list, {{a,b},{c,d}})
```

> uses the quantile parameters `{{a,b},{c,d}}` for the quartiles.

```
QuartileSkewness(matrix)
```

> returns the quartile skewness coefficient of each column of `matrix`.

```
QuartileSkewness(distribution)
```

> returns the quartile skewness coefficient of the `distribution`.

See
* [Wikipedia - Skewness - Quantile-based measures](https://en.wikipedia.org/wiki/Skewness#Quantile-based_measures)

### Examples

```
>> QuartileSkewness({1,2,3,10,100})
113/123

>> QuartileSkewness({7,7,31,31,47,75,87,115,116,119,119,155,177})
-3/11

>> QuartileSkewness({1.0,2.5,3,7,11.5})
0.708333

>> QuartileSkewness({{1,2},{3,4},{5,10},{7,100}})
{0,11/13}

>> QuartileSkewness(NormalDistribution(m, s))
0
```

All three quartiles of constant data are equal, so the coefficient is undefined:

```
>> QuartileSkewness({1,1,1,1})
Indeterminate
```

### Related terms

[InterquartileRange](InterquartileRange.md), [Quantile](Quantile.md), [QuartileDeviation](QuartileDeviation.md), [Quartiles](Quartiles.md), [Skewness](Skewness.md)

### Implementation status

* &#x2705; - full supported

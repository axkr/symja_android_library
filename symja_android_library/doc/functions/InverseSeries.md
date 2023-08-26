## InverseSeries

```
InverseSeries( series )
```

> return the inverse series. 

See:
* [Wikipedia - Taylor series](https://en.wikipedia.org/wiki/Taylor_series)
* [Wikipedia - Big O notation](https://en.wikipedia.org/wiki/Big_O_notation)
* [Wikipedia - Formal power series](https://en.wikipedia.org/wiki/Formal_power_series)

### Examples

```
>> InverseSeries(Series(Sin(x), {x, 0, 7}))
x+x^3/6+3/40*x^5+5/112*x^7+O(x)^8
```
    
### Related terms
[ComposeSeries](ComposeSeries.md) ,[Series](Series.md), [SeriesCoefficient](SeriesCoefficient.md), [SeriesData](SeriesData.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseSeries](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SeriesFunctions.java#L1229) 

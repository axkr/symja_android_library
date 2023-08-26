## StudentTDistribution

```
StudentTDistribution(v)
```

> returns a Student's t-distribution.
    
See
* [Wikipedia - Student's t-distribution](https://en.wikipedia.org/wiki/Student%27s_t-distribution)
 
 
### Examples

The variance of Student's t-distribution is

```
>> Variance(StudentTDistribution(n)) 
Piecewise({{n/(-2+n),n>2}},Indeterminate)
				
>> Variance(StudentTDistribution(4)) 
2
```

### Related terms 
[CDF](CDF.md), [Mean](Mean.md), [Median](Median.md), [PDF](PDF.md), [Quantile](Quantile.md), [StandardDeviation](StandardDeviation.md), [Variance](Variance.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StudentTDistribution](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L6780) 

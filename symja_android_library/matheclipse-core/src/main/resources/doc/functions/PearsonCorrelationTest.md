## PearsonCorrelationTest

```
PearsonCorrelationTest(real-vector1, real-vector2)
```

```
PearsonCorrelationTest(real-vector1, real-vector2, "value")
```

> `"value"` can be `"TestStatistic"`, `"TestData"` or `"PValue"`. In statistics, the Pearson correlation coefficient (PCC) is a correlation coefficient that measures linear correlation between two sets of data.

See
* [Wikipedia - Pearson correlation coefficient](https://en.wikipedia.org/wiki/Pearson_correlation_coefficient)
 
### Examples


```
>> PearsonCorrelationTest({1, 2, 3, 5, 8}, {0.11, 0.12, 0.13, 0.15, 0.18}, \"TestData\") 
{1.0,0.0}
```

 

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of PearsonCorrelationTest](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L6009) 

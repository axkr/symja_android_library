## FiveNum

```
FiveNum({dataset})
```
  
> the Tuckey five-number summary is a set of descriptive statistics that provide information about a `dataset`. It consists of the five most important sample percentiles:

>1. the sample minimum (smallest observation)
>2. the lower quartile or first quartile
>3. the median (the middle value)
>4. the upper quartile or third quartile
>5. the sample maximum (largest observation)
    
See:
* [Wikipedia - Five-number summary](https://en.wikipedia.org/wiki/Five-number_summary)
 

### Examples

``` 
>> FiveNum({0, 0, 1, 2, 63, 61, 27, 13}) 
{0,1/2,15/2,44,63}
```

### Related terms 
[Median](Median.md), [Quantile](Quantile.md), [Quartiles](Quartiles.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FiveNum](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L1877) 

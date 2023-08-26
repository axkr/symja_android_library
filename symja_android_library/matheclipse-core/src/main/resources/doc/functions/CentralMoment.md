## CentralMoment

```
CentralMoment(list, r)
```

> gives the the `r`-th central moment (i.e. the `r`th moment about the mean) of `list`.
  
```
CentralMoment(dist, r)
```

> gives the the `r`-th central moment of the distribution `dist`.

See:  
* [Wikipedia - Central moment](https://en.wikipedia.org/wiki/Central_moment)
 

### Examples

```
>> CentralMoment({1.1, 1.2, 1.4, 2.1, 2.4}, 4)
0.10085
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CentralMoment](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L1374) 

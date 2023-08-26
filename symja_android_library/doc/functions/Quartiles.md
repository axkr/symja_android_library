## Quartiles

```
Quartiles(arg)
```

> returns a list of the `1/4`, `1/2` and `3/4` quantile of `arg`. 

```
Quartiles(arg, {{a,b},{c,d}})
```

> use the quantile parameters `{{a,b},{c,d}}`. The default parameters for the quantile definition are `{{0,0},{1,0}}`. 

See
* [Wikipedia - Quartile](https://en.wikipedia.org/wiki/Quartile)
* [Wikipedia - Quantile](https://en.wikipedia.org/wiki/Quantile)

### Examples

Method 1 from [Wikipedia - Quartile](https://en.wikipedia.org/wiki/Quartile)

```
>> Quartiles({6, 7, 15, 36, 39, 40, 41, 42, 43, 47, 49}, {{0, 0}, {1, 0}}) // N 
{15.0,40.0,43.0} 
```

Method 3 from [Wikipedia - Quartile](https://en.wikipedia.org/wiki/Quartile)

```
>> Quartiles({6, 7, 15, 36, 39, 40, 41, 42, 43, 47, 49}) // N 
{20.25,40.0,42.75}		
```

### Related terms

[FiveNum](FiveNum.md), [Median](Median.md), [Quantile](Quantile.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Quartiles](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L6343) 

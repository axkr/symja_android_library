## BinCounts

```
BinCounts(list, widthOfBin)
```

> count the number of elements, if `list`, is divided into successive bins with width `widthOfBin`.

```
BinCounts(list, {min, max, widthOfBin})
```

> returns the count of elements in `list` that fall within each bin defined by the range from `min` to `max` with bins of width `widthOfBin`.

### Examples

```
>> BinCounts({1,2,3,4,5},5) 
{4,1}

>> BinCounts({1,2,3,4,5},10) 
{5}

>> BinCounts[{4, 3, 1, 2, 4, 3, 6, 4}, {0, 10, 2}]
{2,5,1,0,0}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BinCounts](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L423) 

## BinCounts

```
BinCounts(list, width-of-bin)
```

or 

```
BinCounts(list, {min, max, width-of-bin} )
```

> count the number of elements, if `list`, is divided into successive bins with width `width-of-bin`.

### Examples

```
>> BinCounts({1,2,3,4,5},5) 
{4,1}

>> BinCounts({1,2,3,4,5},10) 
{5}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BinCounts](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L945) 

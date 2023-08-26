## RankedMin

```
RankedMin({e_1, e_2, ..., e_i}, n) 
```

> returns the n-th smallest real value in the list `{e_1, e_2, ..., e_i}`.

```
RankedMin({e_1, e_2, ..., e_i}, -n) 
```

> returns the n-th largest real value in the list `{e_1, e_2, ..., e_i}`.

### Examples


```
>> RankedMin({Pi, Sqrt(3), 2.95, 3}, 3)
3
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RankedMin](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L5576) 

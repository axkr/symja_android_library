## Total

```
Total(list)
```

> adds all values in `list`.
 
```
Total(list, n)
```

> adds all values up to level `n`.

```
Total(list, {n})
```

> totals only the values at level `{n}`.
  

### Examples

```
>> Total({1, 2, 3})
6

>> Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}})
{12,15,18}

>> Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}}, 2)
45

>> Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}}, {2})
{6,15,24}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Total](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L7824) 

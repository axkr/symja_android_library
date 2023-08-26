## LengthWhile

```
LengthWhile({e1, e2, ...}, head)
```

> returns the number of elements `ei` at the start of list for which `head(ei)` returns `True`.

### Examples


```
>> LengthWhile({1, 2, 3, 10, 5, 8, 42, 11}, # < 10 &)
3
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LengthWhile](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L4011) 

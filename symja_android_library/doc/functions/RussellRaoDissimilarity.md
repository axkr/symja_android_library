## RussellRaoDissimilarity

```
RussellRaoDissimilarity(u, v)
```

> returns the Russell-Rao dissimilarity between the two boolean 1-D lists `u` and `v`, which is defined as `(n - c_tt) / c_tt` where `n` is `len(u)` and `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
  
### Examples

```
>> RussellRaoDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})
5/7
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RussellRaoDissimilarity](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L2373) 

## MatchingDissimilarity

```
MatchingDissimilarity(u, v)
```

> returns the Matching dissimilarity between the two boolean 1-D lists `u` and `v`, which is defined as `(c_tf + c_ft) / n`, where `n` is `len(u)` and `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.

### Examples

```
>> MatchingDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})
4/7
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MatchingDissimilarity](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L1388) 

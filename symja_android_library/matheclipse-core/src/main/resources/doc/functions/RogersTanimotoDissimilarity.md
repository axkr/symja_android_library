## RogersTanimotoDissimilarity

```
RogersTanimotoDissimilarity(u, v)
```

> returns the Rogers-Tanimoto dissimilarity between the two boolean 1-D lists `u` and `v`, which is defined as `R / (c_tt + c_ff + R)` where n is `len(u)`, `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * (c_tf + c_ft)`.
  
  
### Examples
``` 
>> RogersTanimotoDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})
8/11
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RogersTanimotoDissimilarity](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L2292) 

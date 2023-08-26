## DiceDissimilarity

```
DiceDissimilarity(u, v)
``` 

> returns the Dice dissimilarity between the two boolean 1-D lists `u` and `v`, which is defined as `(c_tf + c_ft) / (2 * c_tt + c_ft + c_tf)`, where n is `len(u)` and `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.   

### Examples
``` 
>> DiceDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})
1/2
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DiceDissimilarity](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L447) 

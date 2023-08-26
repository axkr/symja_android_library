## JacobiMatrix

```
JacobiMatrix(matrix, var)
```

> creates a Jacobian matrix.


### Examples 

```
>> JacobiMatrix({f(u),f(v),f(w),f(x)}, {u,v,w})
{{f'(u),0,0},{0,f'(v),0},{0,0,f'(w)},{0,0,0}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of JacobiMatrix](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L2975) 

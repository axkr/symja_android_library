## HankelMatrix

``` 
HankelMatrix(n)
```

> gives a Hankel matrix with the dimension `n`.

``` 
HankelMatrix(vector)
```

> gives a Hankel matrix with the elements of the `vector`. 

``` 
HankelMatrix(vector1, vector2)
```

> gives a Hankel matrix with the combined elements of the `vector1` and `vector2`. 

### Examples

```
>> HankelMatrix({f, a, b, c, d},{d, y, z})
{{f,a,b},
 {a,b,c},
 {b,c,d},
 {c,d,y},
 {d,y,z}}
```


### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of HankelMatrix](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L2800) 

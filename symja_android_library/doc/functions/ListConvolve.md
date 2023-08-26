## ListConvolve

```
ListConvolve(kernel-list, tensor-list)
```

> create the convolution of the `kernel-list` with `tensor-list`.

### Examples

```
>> ListConvolve({x, y}, {a, b, c, d, e, f})
{b*x+a*y,c*x+b*y,d*x+c*y,e*x+d*y,f*x+e*y}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ListConvolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L328) 

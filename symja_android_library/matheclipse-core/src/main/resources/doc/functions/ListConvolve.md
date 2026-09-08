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






`ListConvolve` is `ListCorrelate` with the kernel reversed on every level, and like it accepts a
kernel and a tensor of any rank:

```
>> ListConvolve({x,y},{a,b,c,d}) == ListCorrelate({y,x},{a,b,c,d})
True

>> ListConvolve({{{1,2},{3,4}},{{5,6},{7,8}}}, ArrayReshape(Range(27),{3,3,3}))
{{{184,220},{292,328}},{{508,544},{616,652}}}
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ListConvolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L479) 

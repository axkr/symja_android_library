## ListCorrelate

```
ListCorrelate(kernel-list, tensor-list)
```

> create the correlation of the `kernel-list` with `tensor-list`.

### Examples

```
>> ListCorrelate({{u, v}, {w, x}}, {{a,b,c,p}, {d,e,f,q}, {g, h, i,r}})
{{a*u+b*v+d*w+e*x,b*u+c*v+e*w+f*x,c*u+p*v+f*w+q*x},{d*u+e*v+g*w+h*x,e*u+f*v+h*w+i*x,f*u+q*v+i*w+r*x}}
```

The kernel and the tensor may have any rank. An all-ones kernel gives the moving block sums:

```
>> ListCorrelate({{{1,1},{1,1}},{{1,1},{1,1}}}, ArrayReshape(Range(27),{3,3,3}))
{{{60,68},{84,92}},{{132,140},{156,164}}}
```

The result is smaller than the tensor by the size of the kernel:

```
>> Dimensions(ListCorrelate(Array(k,{2,2,2}), Array(f,{4,3,5})))
{3,2,4}
```






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ListCorrelate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L565) 

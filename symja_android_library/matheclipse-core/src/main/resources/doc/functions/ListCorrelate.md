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






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ListCorrelate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L408) 

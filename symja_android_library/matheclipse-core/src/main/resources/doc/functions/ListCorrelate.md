## ListCorrelate

```
ListCorrelate(kernel-list, tensor-list)
```

> create the correlation of the `kernel-list` with `tensor-list`.

### Examples

```
>> ListCorrelate({x, y}, {a, b, c, d, e, f}) 
{a*x+b*y,b*x+c*y,c*x+d*y,d*x+e*y,e*x+f*y}
```

### Github

* [Implementation of ListCorrelate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L303) 

## TensorProduct

```
TensorProduct(t1, t2, ...)
```

> product of the tensors `t1, t2, ...`.


See  
* [Wikipedia - Tensor product](https://en.wikipedia.org/wiki/Tensor_product)

### Examples

```
>> TensorProduct({{{1,2,3},{4,5,6},{7,8,9}},{{2,0,0},{0,3,0},{0,0,1}}},{{2,1,4},{0,3,0},{0,0,1}})
{{{{{2,1,4},{0,3,0},{0,0,1}},{{4,2,8},{0,6,0},{0,0,2}},{{6,3,12},{0,9,0},{0,0,3}}},{{{
8,4,16},{0,12,0},{0,0,4}},{{10,5,20},{0,15,0},{0,0,5}},{{12,6,24},{0,18,0},{0,0,
6}}},{{{14,7,28},{0,21,0},{0,0,7}},{{16,8,32},{0,24,0},{0,0,8}},{{18,9,36},{0,27,
0},{0,0,9}}}},{{{{4,2,8},{0,6,0},{0,0,2}},{{0,0,0},{0,0,0},{0,0,0}},{{0,0,0},{0,
0,0},{0,0,0}}},{{{0,0,0},{0,0,0},{0,0,0}},{{6,3,12},{0,9,0},{0,0,3}},{{0,0,0},{0,
0,0},{0,0,0}}},{{{0,0,0},{0,0,0},{0,0,0}},{{0,0,0},{0,0,0},{0,0,0}},{{2,1,4},{0,
3,0},{0,0,1}}}}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TensorProduct](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L894) 

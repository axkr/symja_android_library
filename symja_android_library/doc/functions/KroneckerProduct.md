## KroneckerProduct

```
KroneckerProduct(t1, t2, ...)
```

> Kronecker product of the tensors `t1, t2, ...`.
	 
See  
* [Wikipedia - Kronecker product](https://en.wikipedia.org/wiki/Kronecker_product)
 
### Examples

```
>> ta = {{1,4,-7}, {-2,3,3}}; tb = {{8,-9,-6,5},{1,-3,-4,7},{2,8,-8,-3},{1,2,-5,-1}}; 
    
>> KroneckerProduct(ta, tb) // MatrixForm
{{8,-9,-6,5,32,-36,-24,20,-56,63,42,-35},
 {1,-3,-4,7,4,-12,-16,28,-7,21,28,-49},
 {2,8,-8,-3,8,32,-32,-12,-14,-56,56,21},
 {1,2,-5,-1,4,8,-20,-4,-7,-14,35,7},
 {-16,18,12,-10,24,-27,-18,15,24,-27,-18,15},
 {-2,6,8,-14,3,-9,-12,21,3,-9,-12,21},
 {-4,-16,16,6,6,24,-24,-9,6,24,-24,-9},
 {-2,-4,10,2,3,6,-15,-3,3,6,-15,-3}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of KroneckerProduct](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L202) 

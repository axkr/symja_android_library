## Adjugate

``` 
Adjugate(matrix)
```

> calculate the adjugate matrix `Inverse(matrix)*Det(matrix)`

See:
* [Wikipedia - Adjugate matrix](https://en.wikipedia.org/wiki/Adjugate_matrix)

### Examples

The 3 × 3 matrix example from Wikipedia:

```
>> Adjugate({{-3,2,-5}, {-1,0,-2}, {3,-4,1}}) 
{{-8,18,-4},
 {-5,12,-1},
 {4,-6,2}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Adjugate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L693) 

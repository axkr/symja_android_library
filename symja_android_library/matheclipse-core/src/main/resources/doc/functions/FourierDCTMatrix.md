## FourierDCTMatrix

``` 
FourierDCTMatrix(n)
```

> gives a discrete cosine transform matrix with the dimension `(n,n)` and method `DCT-2`

``` 
FourierDCTMatrix(n, methodNumber)
```

> gives a discrete cosine transform matrix with the dimension `(n,n)` and method `DCT`-`methodNumber`

See
* [Wikipedia - Discrete cosine transform](https://en.wikipedia.org/wiki/Discrete_cosine_transform) 

### Examples

```
>> FourierDCTMatrix(3, 1)
{{1/2,1/2,1/2}, 
 {1,0,-1}, 
 {1/2,-1/2,1/2}}
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FourierDCTMatrix](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/FourierDCTMatrix.java#L15) 

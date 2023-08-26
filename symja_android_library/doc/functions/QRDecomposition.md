## QRDecomposition

```
QRDecomposition(A)
```

> computes the QR decomposition of the matrix `A`. The QR decomposition is a decomposition of a matrix `A` into a product `A = Q.R` of an unitary matrix `Q` and an upper triangular matrix `R`. 

See 
* [Wikipedia - QR decomposition](https://en.wikipedia.org/wiki/QR_decomposition)

### Examples

```
>> QRDecomposition({{1, 2}, {3, 4}, {5, 6}})
{
{{-0.16903085094570325,0.8970852271450604,0.4082482904638636},
 {-0.50709255283711,0.2760262237369421,-0.8164965809277258},
 {-0.8451542547285165,-0.3450327796711773,0.40824829046386274}},
{{-5.916079783099616,-7.437357441610944},
 {0.0,0.828078671210824},
 {0.0,0.0}}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of QRDecomposition](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L4678) 

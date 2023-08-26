## UnitaryMatrixQ

```
UnitaryMatrixQ(U)
```

> returns `True` if a complex square matrix `U` is unitary, that is, if its conjugate transpose `U^(*)` is also its inverse, that is, if `U^(*).U = U.U^(*) = U.U^(-1) - 1 = I` where `I` is the identity matrix. 

See:  
* [Wikipedia - Unitary matrix](https://en.wikipedia.org/wiki/Unitary_matrix)  

### Examples

```
>> u =  {{0, I}, {I, 0}};

>> UnitaryMatrixQ(u)
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of UnitaryMatrixQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L1469) 

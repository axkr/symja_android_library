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

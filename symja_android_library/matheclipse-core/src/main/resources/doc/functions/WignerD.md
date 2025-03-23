## WignerD

```
WignerD({j,m1,m2},a,b,c)
```

> the Wigner D-function returns the matrix element of a rotation operator.

See:  
* [Wikipedia - Wigner D-matrix](https://en.wikipedia.org/wiki/Wigner_D-matrix)   

### Examples

```  
>> WignerD({3/2,-3/2,-3/2}, ps,th,ph)
Cos(th/2)^3/E^(I*3/2*ph+I*3/2*ps)
```

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of WignerD](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/QuantumPhysicsFunctions.java#L168) 

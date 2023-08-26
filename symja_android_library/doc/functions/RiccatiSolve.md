## RiccatiSolve

```
RiccatiSolve({A,B},{Q,R})
```

> An algebraic Riccati equation is a type of nonlinear equation that arises in the context of infinite-horizon optimal control problems in continuous time or discrete time. The continuous time algebraic Riccati equation (CARE): `A^{T}·X+X·A-X·B·R^{-1}·B^{T}·X+Q==0`. And the respective linear controller is: `K = R^{-1}·B^{T}·P`. The solver receives `A`, `B`, `Q` and `R` and computes `P`.

See:
* [Wikipedia - Algebraic Riccati equation](https://en.wikipedia.org/wiki/Algebraic_Riccati_equation)

### Examples

```
>> RiccatiSolve({ {{3, -2}, {4, -1}}, {{0}, {1}} }, { {{1.0,0.0},{0.0,1.0}}, {{1.0}} }) 
{{19.75982, -7.64298},
 {-7.64298, 4.70718}}
```






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of RiccatiSolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L4779) 

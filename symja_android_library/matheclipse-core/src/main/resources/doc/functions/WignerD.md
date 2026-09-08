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

With two angles the Euler angle `a` is `0`, with one angle both `a` and `c` are `0`, so that only 
the small Wigner d-function remains:

```
>> WignerD({1/2, 1/2, -1/2}, b, c)
Sin(b/2)/E^(I*1/2*c)

>> WignerD({1/2, 1/2, -1/2}, b)
Sin(b/2)
```

The expression stays unevaluated unless `j` is a non-negative integer or half-integer and both 
`m1` and `m2` lie in `-j, ..., j` with `j-m1` and `j-m2` integers:

```
>> WignerD({1, 1/2, 1/2}, ps,th,ph)
WignerD({1,1/2,1/2},ps,th,ph)
```

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of WignerD](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/QuantumPhysicsFunctions.java#L168) 

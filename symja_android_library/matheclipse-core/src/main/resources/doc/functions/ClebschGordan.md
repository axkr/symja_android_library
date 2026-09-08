## ClebschGordan 

```
ClebschGordan({j1,m1},{j2,m2},{j3,m3})
```

> get the Clebsch–Gordan coefficients. Clebsch–Gordan coefficients are numbers that arise in angular momentum coupling in quantum mechanic.

See:  
* [Wikipedia - Clebsch-Gordan coefficients](https://en.wikipedia.org/wiki/Clebsch%E2%80%93Gordan_coefficients)  
* [archive.org - Angular momentum in quantum mechanics](https://archive.org/details/angularmomentumi0000edmo/page/n5/mode/2up)

### Examples

```  
>> ClebschGordan({3/2, -3/2}, {3/2, 3/2}, {1, 0}) 
3/(2*Sqrt(5))
```

The coefficient is `0` unless the three angular momenta couple, which requires `j1+j2+j3` to be an 
integer and each of `j1-m1`, `j2-m2`, `j3-m3` to be an integer as well:

```
>> ClebschGordan({1/2, 1/2}, {1, 0}, {1, 1/2})
0
```

If exactly one of the three projections is a symbol, the selection rule `m1+m2==m3` forces its 
value and the result is reported as a `Piecewise` expression:

```
>> ClebschGordan({1, m}, {1, 0}, {2, 1})
Piecewise({{1/Sqrt(2),m==1}},0)
```

An inexact argument makes the result numerical, at the precision of that argument:

```
>> ClebschGordan({1.0, 0}, {1, 0}, {2, 0})
0.816497
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ClebschGordan](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/QuantumPhysicsFunctions.java#L31) 

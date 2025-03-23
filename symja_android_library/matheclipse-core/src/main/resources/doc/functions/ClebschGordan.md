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
3/2*1/Sqrt(5)
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ClebschGordan](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/QuantumPhysicsFunctions.java#L31) 

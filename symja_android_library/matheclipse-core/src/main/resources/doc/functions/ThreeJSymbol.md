## ThreeJSymbol

```
ThreeJSymbol({j1,m1},{j2,m2},{j3,m3})
```

> get the 3-j symbol coefficients.

See:  
* [Wikipedia - 3-j symbol](https://en.wikipedia.org/wiki/3-j_symbol)   

### Examples

```  
>> ThreeJSymbol({3/2, -3/2}, {3/2, 3/2}, {1, 0}) 
Sqrt(3/5)/2
```

The symbol is `0` unless `m1+m2+m3==0`, the three angular momenta fulfill the triangle relation 
with an integer sum `j1+j2+j3`, and each of `j1-m1`, `j2-m2`, `j3-m3` is an integer:

```
>> ThreeJSymbol({1/2, 1/2}, {1, 0}, {1, -1/2})
0

>> ThreeJSymbol({1, 1/2}, {1, -1/2}, {1, 0})
0
```

If exactly one of the three projections is a symbol, the selection rule `m1+m2+m3==0` forces its 
value and the result is reported as a `Piecewise` expression:

```
>> ThreeJSymbol({1, m}, {1, 0}, {2, 1})
Piecewise({{-1/Sqrt(10),m==-1}},0)
```

An inexact argument makes the result numerical, at the precision of that argument:

```
>> ThreeJSymbol({2.0, 0}, {6, 0}, {4, 0})
0.186989
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ThreeJSymbol](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/QuantumPhysicsFunctions.java#L70) 

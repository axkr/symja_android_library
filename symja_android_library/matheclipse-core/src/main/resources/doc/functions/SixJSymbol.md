## SixJSymbol

```
SixJSymbol({j1,j2,j3},{j4,j5,j6})
```

> get the 6-j symbol coefficients.

See:  
* [Wikipedia - 6-j symbol](https://en.wikipedia.org/wiki/6-j_symbol)   

### Examples

```  
>> SixJSymbol({1, 2, 3}, {1, 2, 3})
1/105
```

The symbol is `0` unless each of the four triples `{j1,j2,j3}`, `{j1,j5,j6}`, `{j4,j2,j6}` and 
`{j4,j5,j3}` couples, which requires the triangle relation and an integer sum:

```
>> SixJSymbol({1/2, 1, 1}, {1, 1, 1})
0
```

An inexact argument makes the result numerical, at the precision of that argument:

```
>> SixJSymbol({1.0, 2.0, 1.0}, {2, 3, 2})
0.0436436
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of SixJSymbol](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/QuantumPhysicsFunctions.java#L103) 

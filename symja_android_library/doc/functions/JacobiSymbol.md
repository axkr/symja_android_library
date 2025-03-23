## JacobiSymbol

```
JacobiSymbol(m, n)
```

> calculates the Jacobi symbol.

See
* [Wikipedia - Jacobi symbol](https://en.wikipedia.org/wiki/Jacobi_symbol)
* [Wikipedia - Legendre symbol](https://en.wikipedia.org/wiki/Legendre_symbol)

### Examples

```
>> JacobiSymbol(1001, 9907)
-1
```

The `JacobiSymbol(a, n)` is a generalization of the Legendre symbol that allows for a composite second (bottom) argument n, although n must still be odd and positive. 

```
>> JacobiSymbol(12345, 331)
-1
```


### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of JacobiSymbol](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L3463) 

## JacobiP

```
JacobiP(n, a, b, z)
```

> returns the Jacobi polynomial. 
   

See
* [Wikipedia - Jacobi polynomials](https://en.wikipedia.org/wiki/Jacobi_polynomials) 

### Examples

```
>> JacobiP(2, a, b, z)
1/4*(1/2*(1+b)*(2+b)*(1-z)^2+(2+a)*(2+b)*(-1+z)*(1+z)+1/2*(1+a)*(2+a)*(1+z)^2)
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of JacobiP](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PolynomialFunctions.java#L1842) 

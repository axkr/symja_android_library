## BellY

```
BellY(n, k, {x1, x2, ... , xN}) 
```

> the second kind of Bell polynomials (incomplete Bell polynomials).

```
BellY(n, k, m) 
```

> the generalized partial Bell polynomial of a matrix `m`. This is equivalent to `BellY(ms)` where `ms` prepends `UnitVector(n, k)` to `m` as a first column.

```
BellY(m) 
```

> the generalized Bell polynomial of a matrix `m` (the iterated Faà di Bruno composition of the columns of `m`).

See:  
* [Wikipedia - Bell polynomials](https://en.wikipedia.org/wiki/Bell_polynomials)

## Examples

```
>> BellY(6, 2, {x1, x2, x3, x4, x5})
10*x3^2+15*x2*x4+6*x1*x5

>> BellY(4, 2, {{x1}, {x2}, {x3}})
3*x2^2+4*x1*x3
```






### Implementation status

* &#x2705; - full support

### Github

* [Implementation of BellY](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/BellY.java) 

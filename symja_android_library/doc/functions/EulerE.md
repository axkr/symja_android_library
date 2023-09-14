## EulerE

```
EulerE(n)
```

> gives the euler number `En`.

```
EulerE(n, x)
```

> gives the Eulerian polynomial.

See
* [Wikipedia - Eulerian polynomials](https://en.wikipedia.org/wiki/Eulerian_number)
* [Wikipedia - Euler number](https://en.wikipedia.org/wiki/Euler_numbers)

### Examples

```
>> EulerE(6)
-61

>> EulerE(10,z)
-155*z+255*z^3-126*z^5+30*z^7-5*z^9+z^10
```
 
### Github

* [Implementation of EulerE](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L1653) 

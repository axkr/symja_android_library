## MoebiusMu

```
MoebiusMu(expr)
```

> calculate the Möbius function.


* `MoebiusMu(n) = 1` if `n` is a square-free integer with an even number of prime factors.
* `MoebiusMu(n) = −1` if `n` is a square-free integer with an odd number of prime factors.
* `MoebiusMu(n) = 0` if `n` has a squared prime factor.

See:
* [Wikipedia - Möbius function](https://en.wikipedia.org/wiki/M%C3%B6bius_function)

### Examples

```
>> MoebiusMu(30)
-1

>> FactorInteger(30)
{{2,1},{3,1},{5,1}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MoebiusMu](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L3543) 

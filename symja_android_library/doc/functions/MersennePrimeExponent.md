## MersennePrimeExponent

```
MersennePrimeExponent(n)
```

> returns the  `n`th mersenne prime exponent. `2^n - 1` must be a prime number. 
Currently `0 <= n <= 47` can be computed, otherwise the function returns unevaluated.

See
* [Wikipedia - Mersenne prime](https://en.wikipedia.org/wiki/Mersenne_prime)
* [Wikipedia - List of perfect numbers](https://en.wikipedia.org/wiki/List_of_perfect_numbers)

### Examples

```
>> Table(MersennePrimeExponent(i), {i,20})
{2,3,5,7,13,17,19,31,61,89,107,127,521,607,1279,2203,2281,3217,4253,4423}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MersennePrimeExponent](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L3391) 

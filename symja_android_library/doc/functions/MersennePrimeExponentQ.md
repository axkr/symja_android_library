## MersennePrimeExponentQ

```
MersennePrimeExponentQ(n)
```

> returns `True` if `2^n - 1` is a prime number. Currently `0 <= n <= 47` can be computed in reasonable time.

See
* [Wikipedia - Mersenne prime](https://en.wikipedia.org/wiki/Mersenne_prime)
* [Wikipedia - List of perfect numbers](https://en.wikipedia.org/wiki/List_of_perfect_numbers)

### Examples

```
>> Select(Range(10000), MersennePrimeExponentQ)
{2,3,5,7,13,17,19,31,61,89,107,127,521,607,1279,2203,2281,3217,4253,4423,9689,9941}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MersennePrimeExponentQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L3439) 

## NextPrime

```
NextPrime(n)
```

> gives the next prime after `n`.
	
```
NextPrime(n, k)
```

> gives the `k`th  prime after `n`.

The negatives of the primes count: the primes are ordered `..., -5, -3, -2, 2, 3, 5, ...`. A negative `k` counts downwards; a real `n` starts from `Floor(n)` going up and from `Ceiling(n)` going down. `NextPrime(n, 0)` is `n` if `n` or `-n` is a prime.

### Examples

```
>> NextPrime(10000)
10007
>> NextPrime(100, -5)
73
>> NextPrime(10, -5)
-2
>> NextPrime(100, 5)
113
>> NextPrime(5.5, 100)
563
>> NextPrime(5, 10.5)
NextPrime(5, 10.5)

>> NextPrime(-10)
-7
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NextPrime](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L4458) 

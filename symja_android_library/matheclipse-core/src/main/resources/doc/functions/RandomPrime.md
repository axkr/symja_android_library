## RandomPrime

```
RandomPrime({imin, imax})
```

> create a random prime integer number between `imin` and `imax` inclusive.

```
RandomPrime(imax)
```

> create a random prime integer number between `2` and `imax` inclusive.
 
```
RandomPrime(range, n)
```

> gives a list of `n` random primes in `range`.
 
    
### Examples

```
>> RandomPrime(100000000000000000000000000)
338802421239407

>> RandomPrime({14, 17})
17

>> RandomPrime({14, 16}, 1)
 :: There are no primes in the specified interval.
RandomPrime({14, 16}, 1)

>> RandomPrime({8,12}, 3)
{11, 11, 11}

>> RandomPrime({10,30}, {2,5})
{{13,13,23,29,23},{13,11,23,13,11}}

>> RandomPrime({10,12}, {2,2})
{{11, 11}, {11, 11}}

>> RandomPrime(2, {3,2})
{{2, 2}, {2, 2}, {2, 2}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RandomPrime](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/RandomFunctions.java#L472) 

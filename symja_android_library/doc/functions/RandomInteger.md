## RandomInteger

```
RandomInteger(n)
```

> create a random integer number between `0` and `n`.

```
RandomInteger(dist)
```

> gives a random integer drawn from the discrete distribution `dist`. It is the legacy form of `RandomVariate(dist)`.

```
RandomInteger(dist, n)
RandomInteger(dist, {n1, n2, ...})
```

> gives a list or an `n1 x n2 x ...` array of random integers drawn from `dist`.
 
### Examples

```
>> RandomInteger(100)
88
```

```
>> RandomInteger(PoissonDistribution(3), 5)
{2,4,1,3,3}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RandomInteger](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/RandomFunctions.java#L394) 

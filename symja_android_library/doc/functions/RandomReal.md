## RandomReal

```
RandomReal()
```

> create a random number between `0.0` and `1.0`.

```
RandomReal(dist)
```

> gives a random real number drawn from the continuous distribution `dist`. It is the legacy form of `RandomVariate(dist)`.

```
RandomReal(dist, n)
RandomReal(dist, {n1, n2, ...})
```

> gives a list or an `n1 x n2 x ...` array of random real numbers drawn from `dist`.
 
### Examples

```
>> RandomReal( )
0.53275
```

```
>> RandomReal(NormalDistribution(0,1), 3)
{-0.41572,1.22079,0.08834}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RandomReal](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/RandomFunctions.java#L646) 

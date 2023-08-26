## RandomPermutation

```
RandomPermutation(s)
```

> create a pseudo random permutation between `1` and `s`.
 
```
RandomPermutation(s, n)
```

> create `n` pseudo random permutations.
 
See 
* [Wikipedia - Permutation](https://en.wikipedia.org/wiki/Permutation)

### Examples

```
>> RandomPermutation(10)
Cycles({{1,2,7,3,8,10,5,9,4,6}})

>> RandomPermutation(10,3) 
{Cycles({{1,6,4,5,7,10,9,2,3,8}}),Cycles({{1,10,9,4,2,6,3,8,7,5}}),Cycles({{1,4,2,6,8,9,5,7,10,3}})}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RandomPermutation](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/RandomFunctions.java#L440) 

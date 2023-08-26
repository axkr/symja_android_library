## HammingDistance

``` 
HammingDistance(a, b)
```

> returns the Hamming distance of `a` and `b`, i.e. the number of different elements.

See:
* [Wikipedia - Hamming distance](https://en.wikipedia.org/wiki/Hamming_distance)
* [Youtube - Hamming codes and error correction](https://youtu.be/X8jsijhllIA)
* [Youtube - Hamming codes part 2, the elegance of it all](https://youtu.be/b3NxrZOu_CE)

### Examples

```
>> HammingDistance("time", "dime")
1

```

The `IgnoreCase` option makes `EditDistance` ignore the case of letters:

``` 
>> HammingDistance("TIME", "dime", IgnoreCase -> True)
1
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of HammingDistance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L669) 

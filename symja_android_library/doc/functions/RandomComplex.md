## RandomComplex

```
RandomComplex[{z_min, z_max}]
```

> yields a pseudo-random complex number in the rectangle with complex corners `z_min` and `z_max`.

```
RandomComplex[z_max]
```

> yields a pseudo-random complex number in the rectangle with corners at the origin and at `z_max`.

```
RandomComplex()
```

> yields a pseudo-random complex number with real and imaginary parts from `0` to `1`.

```
RandomComplex(range, n)
```

> gives a list of `n` pseudo-random complex numbers.

```
RandomComplex(range, {n1, n2, ...})
```

> gives a nested list

### Examples

```
>> RandomComplex( ) 
0.313565+I*0.954076

>> RandomComplex({1+I, 2+2I}, {2,2,3}) 
{{{1.61894+I*1.62895,1.42982+I*1.64042,1.88055+I*1.24075},{1.27681+I*1.09553,1.29139+I*1.79987,1.47368+I*1.59429}},{{1.42116+I*1.54729,1.51395+I*1.05403,1.47495+I*1.7832},{1.79694+I*1.1428,1.93639+I*1.50855,1.51072+I*1.02286}}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RandomComplex](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/RandomFunctions.java#L245) 

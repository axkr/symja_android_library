## Mod

```
Mod(x, m)
```

> returns `x` modulo `m`.
 
See
* [Wikipedia - Modulo operation](https://en.wikipedia.org/wiki/Modulo_operation)

### Examples

```
>> Mod(14, 6)
2

>> Mod(-3, 4)
1

>> Mod(-3, -4)
-3
```

For Gaussian integers the remainder is `x - m*Round(x/m)`, rounding the real and imaginary parts
separately, so the result is the representative of least norm:

```
>> Mod(7 + 3*I, 2)
-1-I

>> Mod(7, 2 + I)
-I
```

The argument 0 should be nonzero

```
>> Mod(5, 0) 
Mod(5, 0)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Mod](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntegerFunctions.java#L1458) 

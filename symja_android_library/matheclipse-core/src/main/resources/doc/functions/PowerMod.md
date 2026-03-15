## PowerMod

```
PowerMod(x, y, m)
```

> computes `x^y` modulo `m`. `x` and `m` must be Gaussian integers and the `y` must be an integer or rational number.

See
* [Wikipedia - Exponentiation](https://en.wikipedia.org/wiki/Modular_exponentiation)

### Examples

```
>> PowerMod(2, 10000000, 3)
1
>> PowerMod(3, -2, 10)
9
```

0 is not invertible modulo 2.

```
>> PowerMod(0, -1, 2)
PowerMod(0, -1, 2)
```

The third argument of `PowerMod` should be nonzero.

```
>> PowerMod(5, 2, 0)
 PowerMod(5, 2, 0)
```

`PowerMod` works on square roots.

```
>> PowerMod(3,1/2,2)
1
```

`PowerMod` works on Gaussian integers.

```
>> PowerMod(2+I,2,19)
3+I*4
```

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PowerMod](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntegerFunctions.java#L1648) 

## GCD

```
GCD(n1, n2, ...)
```

> computes the greatest common divisor of the given integers. 

See
* [Wikipedia - Greatest common divisor](https://en.wikipedia.org/wiki/Greatest_common_divisor) 
* [Fungrim - Greatest common divisor](http://fungrim.org/topic/Greatest_common_divisor/)

### Examples

```
>> GCD(20, 30)
10
>> GCD(10, y)
GCD(10, y)
```

`GCD` is `Listable`:

```
>> GCD(4, {10, 11, 12, 13, 14})
{2, 1, 4, 1, 2}
```

### Related terms 
[LCM](LCM.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of GCD](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L1803) 

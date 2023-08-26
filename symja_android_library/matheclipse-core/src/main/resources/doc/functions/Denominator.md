## Denominator

```
Denominator(expr)
```

> gives the denominator in `expr`. Denominator collects expressions with negative exponents.

See
* [Wikipedia - Fraction (mathematics)](https://en.wikipedia.org/wiki/Fraction_(mathematics))

### Examples

```
>> Denominator(a / b)
b
>> Denominator(2 / 3)
3
>> Denominator(a + b)
1
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Denominator](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L1140) 

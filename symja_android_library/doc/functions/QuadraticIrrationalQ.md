## QuadraticIrrationalQ

```
QuadraticIrrationalQ(expr)
```

> returns `True`, if the `expr` is of the form `(p + s * Sqrt(d)) / q` for integers `p,q,d,s`.

See
* [Wikipedia - Quadratic irrational number](https://en.wikipedia.org/wiki/Quadratic_irrational_number)
* [Wikipedia - Periodic continued fraction](https://en.wikipedia.org/wiki/Periodic_continued_fraction)

### Examples

```
>> QuadraticIrrationalQ(5*Sqrt(11))
True

>> QuadraticIrrationalQ((7*Sqrt(2) + 1)/11)
True

>> QuadraticIrrationalQ(42)
False
```

### Related terms

[ContinuedFraction](ContinuedFraction.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of QuadraticIrrationalQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L4548) 

## EvenQ

```
EvenQ(x)
```

> returns `True` if `x` is even, and `False` otherwise.


```
EvenQ(x, GaussianIntegers->True)
```

> returns `True` if `x` is even and a Gaussian integer number, and `False` otherwise.

See
* [Wikipedia - Parity (mathematics)](https://en.wikipedia.org/wiki/Parity_(mathematics))


### Examples

```
>> EvenQ(4)
True

>> EvenQ(-3)
False

>> EvenQ(n)
False

>> EvenQ(2+4*I, GaussianIntegers->True)
True

>> EvenQ(1+I, GaussianIntegers->True)
False
```

### Related terms 
[Odd](Odd.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of EvenQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L461) 

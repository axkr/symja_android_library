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
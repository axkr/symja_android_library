## OddQ

```
OddQ(x)
```

> returns `True` if `x` is odd, and `False` otherwise.

```
OddQ(x, GaussianIntegers->True)
```

> returns `True` if `x` is odd and a Gaussian integer number, and `False` otherwise.

See
* [Wikipedia - Parity (mathematics)](https://en.wikipedia.org/wiki/Parity_(mathematics))

### Examples

```
>> OddQ(-3)
True

>> OddQ(0)
False

>> OddQ(1+4*I, GaussianIntegers->True)
True

>> OddQ(2+4*I, GaussianIntegers->True)
False
```



### Related terms 
[EvenQ](EvenQ.md)
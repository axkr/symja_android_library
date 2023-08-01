## NumericalOrder

```
NumericalOrder(a, b)
```

> is `0` if `a` equals `b`. Is `-1` or `1` according to numerical order of `a` and `b`.
 
See
* [Wikipedia - Order theory](https://en.wikipedia.org/wiki/Order_theory)

### Examples

```
>> NumericalOrder(3,4)
1

>> NumericalOrder(4,3)
-1

>> NumericalOrder(Infinity,4)
-1

>> NumericalOrder(-Infinity,3/4)
1

```

### Related terms 
[NumericalSort](NumericalSort.md), [Order](Order.md), [Sort](Sort.md)

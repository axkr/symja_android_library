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






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NumericalOrder](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L1392) 

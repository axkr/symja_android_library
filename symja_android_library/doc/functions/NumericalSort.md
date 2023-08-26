## NumericalSort

```
NumericalSort(list)
```

> `NumericalSort(list)` is evaluated by calling `Sort(list, NumericalOrder)`.
 
See
* [Wikipedia - Order theory](https://en.wikipedia.org/wiki/Order_theory)

### Examples

```
>> NumericalSort({1,2,3,Infinity,-Infinity,E,Pi,GoldenRatio,Degree}) 
{-Infinity,Pi/180,1,GoldenRatio,2,E,3,Pi,Infinity}
```

### Related terms 
[NumericalOrder](NumericalOrder.md), [Order](Order.md), [Sort](Sort.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NumericalSort](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L1408) 

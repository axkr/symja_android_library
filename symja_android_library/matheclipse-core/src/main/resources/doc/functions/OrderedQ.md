## OrderedQ

```
OrderedQ({a, b,...})
```

> is `True` if `a` sorts before `b` according to canonical ordering for all adjacent elements.

```
OrderedQ({a, b,...}, comparator)
```

> is `True` if `a` sorts before `b` according to the `comparator` functions value for all adjacent elements. If the `comparator` function returns `False` or `-1` the `OrderedQ` function returns `False`.  If the `comparator` function doesn't return `False` or `-1` for all adjacent elements the `OrderedQ` function returns `True`.

See
* [Wikipedia - Order theory](https://en.wikipedia.org/wiki/Order_theory)

### Examples

```
>> OrderedQ({a, b})
True

>> OrderedQ({b, a})
False
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of OrderedQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L1447) 

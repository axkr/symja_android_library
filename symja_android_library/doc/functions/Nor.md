## Nor

```
Nor(arg1, arg2, ...)'
```

> Logical NOR function. It evaluates its arguments in order, giving `False` immediately if any of them are `True`, and `True` if they are all `False`.
 
See 
* [Wikipedia - Logical NOR](https://en.wikipedia.org/wiki/Logical_NOR)

### Examples

```
>> Nor(False, False, False)
True
 
>> Nor(False, True, a)
False
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Nor](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L3738) 

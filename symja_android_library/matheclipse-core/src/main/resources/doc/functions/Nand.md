## Nand

```
Nand(arg1, arg2, ...)
```

> Logical NAND function. It evaluates its arguments in order, giving `True` immediately if any of them are `False`, and `False` if they are all `True`.

See 
* [Wikipedia - Sheffer stroke](https://en.wikipedia.org/wiki/Sheffer_stroke)
 
### Examples

```
>> Nand(True, True, True)
False
 
>> Nand(True, False, a)
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Nand](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L3389) 

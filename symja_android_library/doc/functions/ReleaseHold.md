## ReleaseHold

```
ReleaseHold(expr)
```

> removes any `Hold`, `HoldForm`, `HoldPattern` or `HoldComplete` head from `expr`.
 

### Examples

```
>> x = 3;

>> Hold(x)
Hold(x)

>> ReleaseHold(Hold(x))
3

>> ReleaseHold(y)
y
```

### Related terms 
[Hold](Hold.md), [HoldComplete](HoldComplete.md), [HoldForm](HoldForm.md), [HoldPattern](HoldPattern.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ReleaseHold](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L1436) 

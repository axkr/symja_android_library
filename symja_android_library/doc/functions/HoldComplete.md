## HoldComplete

```
HoldComplete(expr)
```

> `HoldComplete` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its arguments. `HoldComplete` doesn't evaluate `UpValues`.
 

### Examples

``` 
>> HoldComplete(3*2)
HoldComplete(3*2) 

>> Attributes(HoldComplete)
{HoldAllComplete,Protected,SequenceHold}
```

### Related terms 
[Hold](Hold.md), [HoldForm](HoldForm.md), [HoldPattern](HoldPattern.md), [ReleaseHold](ReleaseHold.md), [Unevaluated](Unevaluated.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of HoldComplete](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L767) 

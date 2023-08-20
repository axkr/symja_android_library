## Hold

```
Hold(expr)
```

> `Hold` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its arguments. `HoldComplete` doesn't evaluate `UpValues`.
 

### Examples

``` 
>> Hold(3*2)
Hold(3*2) 

>> Attributes(Hold)
{HoldAll,Protected}
```

### Related terms 
[HoldComplete](HoldComplete.md), [HoldForm](HoldForm.md), [HoldPattern](HoldPattern.md), [ReleaseHold](ReleaseHold.md), [Unevaluated](Unevaluated.md)
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Hold](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L747) 

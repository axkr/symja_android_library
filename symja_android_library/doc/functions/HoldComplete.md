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

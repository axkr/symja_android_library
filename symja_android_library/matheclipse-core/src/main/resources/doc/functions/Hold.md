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
 

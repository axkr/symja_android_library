## SubsetReplace

```
SubsetReplace(list, sublist -> rhs)
```

or

```
SubsetReplace(list, sublist :> rhs)
```

> replaces the sublist pattern expression `sublist` in `list` with the right-hand-side `rhs`.

**Note:** this function doesn't support pattern sequences at the moment.
 
### Examples

```
>> SubsetReplace({a,b,c,d},{x_,y_} :> f(x,y))
{f(a,b),f(c,d)} 
```

### Related terms
[SubsetCases](SubsetCases.md), [SubsetQ](SubsetQ.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of SubsetReplace](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SubsetFunctions.java#L263) 

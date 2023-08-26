## Replace

```
Replace(expr, lhs -> rhs)
```

> replaces the left-hand-side pattern expression `lhs` in `expr` with the right-hand-side `rhs`.

```
Replace(expr, {lhs1 -> rhs1, lhs2 -> rhs2, ... } )
```
 
> replaces the left-hand-side patterns expression `lhsX` in `expr` with the right-hand-side `rhsX`.
 
## Examples

```
>> Replace(x, {{e->q, x -> a}, {x -> b}})
{a,b}
```


### Related terms 
[ReplaceAll](ReplaceAll.md), [ReplaceList](ReplaceList.md), [ReplacePart](ReplacePart.md), [ReplaceRepeated](ReplaceRepeated.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Replace](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L5645) 

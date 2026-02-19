## ReplaceAt

```
ReplaceAt(expr, lhs -> rhs, position)
```

> replaces the given `position` in `expr` which matches `lhs` with `rhs`.

```
ReplaceAt(expr, lhs -> rhs, list-of-positions)
```

> replaces the given `list-of-positions` in `expr` which matches `lhs` with `rhs`.
 
## Examples

```
>> ReplaceAt({a, a, a, a, a}, a -> xx, 2 ;; 5) 
{a,xx,xx,xx,xx}
```


### Related terms 
[ReplaceAll](ReplaceAll.md), [ReplaceList](ReplaceList.md), [ReplacePart](ReplacePart.md), [ReplaceRepeated](ReplaceRepeated.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ReplaceAt](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L994) 

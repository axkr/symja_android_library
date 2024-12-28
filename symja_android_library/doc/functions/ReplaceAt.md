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

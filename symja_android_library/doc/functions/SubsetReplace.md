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
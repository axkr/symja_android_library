## SubsetCases

```
SubsetCases(list, sublist -> rhs)
```

or

```
SubsetCases(list, sublist :> rhs)
```

> returns a list of the right-hand-side `rhs` for the matching sublist pattern expression `sublist` in `list`.
 
```
SubsetCases(list, sublist)
```

> returns a list of the matching sublist pattern expressions `sublist` in `list`.

### Examples

```
>> SubsetCases({a,b,c,d},{x_,y_} :> f(x,y))
{f(a,b),f(c,d)}
        
>> SubsetCases({a,b,c,d},{x_,y_}) 
{{a,b},{c,d}}
```

### Related terms
[SubsetReplace](SubsetReplace.md), [SubsetQ](SubsetQ.md)
## Splice

```
Splice(list-of-elements)
```

> the `list-of-elements` will automatically be converted into a `Sequence` of elements.

```
Splice(list-of-elements, head-pattern)
```

> the `list-of-elements` will automatically be converted into a `Sequence` of elements, if the calling expression matches the `head-pattern`.

### Examples

```
>> h(a, b, c, Splice({{1,1},{2,2}}, h), d, e) 
h(a,b,c,{1,1},{2,2},d,e)
```

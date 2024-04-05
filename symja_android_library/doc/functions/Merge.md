## Merge

```
Merge(list-of-rules-or-associations, function)
```

> use the `function` to merge right-hand-side values with the left-hand-side key in the `list-of-rules-or-associations`.

### Examples

```
>> Merge({<|x->1|>, x->2, x->3, {y->4}}, Identity) 
<|x->{1,2,3},y->{4}|>
```

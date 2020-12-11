## KeySelect

```
KeySelect(<|key1->value1, ...|>, head)
```

> returns an association of the elements  for which `head(keyi)` returns `True`.

```
KeySelect({key1->value1, ...}, head)
```

> returns an association of the elements  for which `head(keyi)` returns `True`.

### Examples

```
>> r = {beta -> 4, alpha -> 2, x -> 4, z -> 2, w -> 0.8}; 

>> KeySelect(r, MatchQ(#,alpha|x)&) 
<|alpha->2,x->4|>
```


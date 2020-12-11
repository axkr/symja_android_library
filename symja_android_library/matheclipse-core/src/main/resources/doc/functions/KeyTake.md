## KeyTake

```
KeyTake(<|key1->value1, ...|>, {k1, k2,...})
```

> returns an association of the rules for which the `k1, k2,...` are keys in the association.

```
KeySelect({key1->value1, ...}, {k1, k2,...})
```

> returns an association of the rules  for which the `k1, k2,...` are keys in the association.

### Examples

```
>> r = {beta -> 4, alpha -> 2, x -> 4, z -> 2, w -> 0.8}; 
				 
>> KeyTake(r, {alpha,x}) 
<|alpha->2,x->4|>
```


## KeyValueMap

```
KeyValueMap(head, association)
```

> returns a list of the rules pairs `{head(k1,v1),head(k2,v2),...}` for which the `k1, k2,...` are the keys and the `v1, v2,...` are the corresponding values in the association.


### Examples

The operator form `KeyValueMap(f)` can be used:

```
>> KeyValueMap(f)[<|a -> 1, b -> 2, c -> 3|>]
{f(a,1),f(b,2),f(c,3)}
```

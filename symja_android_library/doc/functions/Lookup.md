## Lookup

```
Lookup(association, key) 
```

> return the value in the `association` which is associated with the `key`. If no value is available return `Missing("KeyAbsent",key)`.


```
Lookup(association, key, defaultValue) 
```

> return the value in the `association` which is associated with the `key`. If no value is available return `defaultValue`.

### Examples

```
>> Lookup(<|a -> 11, b -> 17|>, a) 
11

>> Lookup(<|a -> 1, b -> 2|>, c) 
Missing(KeyAbsent,c)

>> Lookup(<|a -> 1, b -> 2|>, c, 42) 
42 
```

### Related terms  
[Association](Association.md), [AssociationQ](AssociationQ.md), [Counts](Counts.md), [Keys](Keys.md), [KeySort](KeySort.md), [Values](Values.md)
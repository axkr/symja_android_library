## KeyExistsQ

```
KeyExistsQ(association, key) 
```

> test if the `key` value is a key in the `association`.
 
### Examples

```
>> KeyExistsQ(<|1->U,2->V|>, 1)
True

>> KeyExistsQ(<|1->U,2->V|>, V) 
False
```

### Related terms  
[Association](Association.md), [AssociationMap](AssociationMap.md), [AssociationQ](AssociationQ.md), [AssociationThread](AssociationThread.md), [Counts](Counts.md), [Lookup](Lookup.md), [Keys](Keys.md), [KeySort](KeySort.md), [Values](Values.md)
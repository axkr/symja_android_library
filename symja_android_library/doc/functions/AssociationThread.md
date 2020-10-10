## AssociationThread

```
AssociationThread({k1,k2,...}, {v1,v2,...})
```

> create an association with rules from the keys `{k1,k2,...}` and values `{v1,v2,...}`.

```
AssociationThread({k1,k2,...} -> {v1,v2,...})
```

> create an association with rules from the keys `{k1,k2,...}` and values `{v1,v2,...}`.

### Examples

```  
>> AssociationThread({"U","V"},{1,2}) 
<|U->1,V->2|>

>> AssociationThread({"U","V"} :> {1,2}) 
<|U:>1,V:>2|>
```


### Related terms  
[AssociateTo](AssociateTo.md), [Association](Association.md),  [AssociationQ](AssociationQ.md), [AssociationMap](AssociationMap.md), [Counts](Counts.md), [Lookup](Lookup.md), [KeyExistsQ](KeyExistsQ.md), [Keys](Keys.md), [KeySort](KeySort.md), [Values](Values.md)
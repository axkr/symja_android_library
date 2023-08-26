## AssociationMap

```
AssociationMap(header, <|k1->v1, k2->v2,...|>)
```

> create an association `<|header(k1->v1), header(k2->v2),...|>` with the rules mapped by the `header`.
 
```
AssociationMap(header, {k1, k2,...})
```

> create an association `<|k1->header(k1), k2->header(k2),...|>` with the rules mapped by the `header`.

### Examples

```  
>> AssociationMap(Reverse,<|U->1,V->2|>) 
<|1->U,2->V|>

>> AssociationMap(f,{U,V}) 
<|U->f(U),V->f(V)|>
```

### Related terms  
[Association](Association.md),  [AssociationQ](AssociationQ.md), [AssociationThread](AssociationThread.md), [Counts](Counts.md), [Lookup](Lookup.md), [KeyExistsQ](KeyExistsQ.md), [Keys](Keys.md), [KeySort](KeySort.md), [Values](Values.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AssociationMap](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssociationFunctions.java#L380) 

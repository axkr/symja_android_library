## AssociationQ

```
AssociationQ(expr) 
```

> returns `True` if `expr` is an association, and `False` otherwise.
 
### Examples

```
>> AssociationQ(<|ahey->avalue, bkey->bvalue, ckey->cvalue|>)
True

>> AssociationQ(<|a -> 1, b :> 2|>)
True

>> AssociationQ(<|a, b|>)
False     
```

### Related terms  
[Association](Association.md), [Counts](Counts.md), [Keys](Keys.md), [KeySort](KeySort.md), [Lookup](Lookup.md), [Values](Values.md)

### Github

* [Implementation of AssociationQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/expression/BuiltInSymbol.java#L382) 

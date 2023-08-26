## AssociateTo

```
AssociateTo(assoc, rule)
```

> append `rule` to the association `assoc` and assign the result to `assoc`.

```
AssociateTo(assoc, list-of-rules)
```

> append the `list-of-rules` to the association `assoc` and assign the result to `assoc`.

### Examples

```  
>> assoc = <|"A" -> <|"a" -> 1, "b" -> 2, "c" -> 3|>|> 
<|A-><|a->1,b->2,c->3|>|>

>> AssociateTo(assoc, "A" -> 11)
<|A->11|>  
```

### Related terms
[Association](Association.md),  [AssociationQ](AssociationQ.md), [AssociationMap](AssociationMap.md), [AssociationThread](AssociationThread.md), [Counts](Counts.md), [Lookup](Lookup.md), [KeyExistsQ](KeyExistsQ.md), [Keys](Keys.md), [KeySort](KeySort.md), [Values](Values.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AssociateTo](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssociationFunctions.java#L106) 

## Association

```
Association[key1 -> val1, key2 -> val2, ...>
```

or 

```
<|key1 -> val1, key2 -> val2, ...|>
```

> represents an association between `key`s and `value`s.
 
### Examples

```
>> Association({akey->avalue, bkey->bvalue, ckey->cvalue}) 
<|akey->avalue,bkey->bvalue,ckey->cvalue|> 
```

`Association` is the head of associations:

```
>> Head(<|a -> x, b -> y, c -> z|>)
Association

>> <|a -> x, b -> y|>
<|a -> x, b -> y|>
     
>> Association({a -> x, b -> y})
<|a -> x, b -> y|>
```
   
Associations can be nested:

```    
>> <|a -> x, b -> y, <|a -> z, d -> t|>|>
<|a -> z, b -> y, d -> t|>
```
     
### Related terms  
[AssociationQ](AssociationQ.md), [Counts](Counts.md), [Keys](Keys.md), [KeySort](KeySort.md), [Lookup](Lookup.md), [Values](Values.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Association](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssociationFunctions.java#L243) 

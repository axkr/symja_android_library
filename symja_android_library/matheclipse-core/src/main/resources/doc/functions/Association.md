## Association

```
Association(list-of-rules) 
```

> create a `key->value` association map from the `list-of-rules`.
 
### Examples

```
>> Association({ahey->avalue, bkey->bvalue, ckey->cvalue}) 
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
## KeyDrop

```
KeyDrop(<|key1->value1, ...|>, {k1, k2,...})
```

> `KeyDrop` is a function used to remove specified keys `{k1, k2,...}` and their associated values from an association. It is useful for simplifying or filtering associations by excluding unwanted keys.
 
### Examples

```
>> productInfo = <|"Name" -> "Widget X", "Price" -> 19.99,"InStock" -> True, "SupplierID" -> "SUP123"|>;
				 
>> KeyDrop(productInfo, {"SupplierID", "InStock"}) 
<|Name->Widget X,Price->19.99|>
```

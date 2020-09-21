## AssociateTo

```
AssociateTo(assoc, rule)
```

> append `rule` to the association `assoc` and set `assoc` to the result.

```
AssociateTo(assoc, list-of-rules)
```

> append the `list-of-rules` to the association `assoc` and set `assoc` to the result.

### Examples

```  
>> assoc = <|"A" -> <|"a" -> 1, "b" -> 2, "c" -> 3|>|> 
<|A-><|a->1,b->2,c->3|>|>

>> AssociateTo(assoc, "A" -> 11)
<|A->11|>  
```
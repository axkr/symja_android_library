## KeySort

```
KeySort(<|key1->value1, ...|>)
```

> sort the `<|key1->value1, ...|>` entries by the `key` values.

```
KeySort(<|key1->value1, ...|>, comparator)
```

> sort the entries by the `comparator`.

### Examples

```
>> KeySort(<|2 -> y, 3 -> z, 1 -> x|>) 
<|1->x,2->y,3->z|>

>> KeySort(<|2 -> y, 3 -> z, 1 -> x|>, Greater) 
<|3->z,2->y,1->x|>
```


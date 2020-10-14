## FilterRules 

```
FilterRules(list-of-option-rules, list-of-rules)
```

or 

```
FilterRules(list-of-option-rules, list-of-symbols)
```

> filter the `list-of-option-rules` by `list-of-rules`or `list-of-symbols`.

### Examples

```
>> Options(f) = {a -> 1, b -> 2}
{a->1,b->2}

>> FilterRules({b -> 3, MaxIterations -> 5}, Options(f)) 
{b->3}
```

### Related terms 
[Options](Options.md)
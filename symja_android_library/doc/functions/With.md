## With

```
With({list_of_local_variables}, expr )
```

> evaluates `expr` for the `list_of_local_variables` by replacing the local variables in `expr`.
 
### Examples
 
```
>> With({x = a}, (1 + x^2) &) 
1+a^2&
```

### Related terms 
[Block](Block.md), [Module](Module.md) 
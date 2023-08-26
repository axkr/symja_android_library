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






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of With](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L3579) 

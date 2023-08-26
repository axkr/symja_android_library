## Key

```
Key(key) 
```

> represents a `key` used to access a value in an association. 
 
```
association[[Key(key)]]
```
 
> determine the value associated to `key` in `association`.

### Examples

```
>> <|1 -> a, 3 -> b|>[[Key(3)]]
b
```

### Related terms  
[Association](Association.md), [AssociationQ](AssociationQ.md), [Counts](Counts.md), [Lookup](Lookup.md), [Keys](Keys.md), [KeySort](KeySort.md), [Values](Values.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Key](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssociationFunctions.java#L589) 

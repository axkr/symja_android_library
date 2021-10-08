## BooleanVariables

```
BooleanVariables(logical-expr)
```

> gives a list of the boolean variables that appear in the `logical-expr`.

### Examples

```
>> BooleanVariables(Xor(p,q,r))
{p,q,r}
```

### Related terms 
[Variables](Variables.md)

### Github

* [Implementation of BooleanVariables](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L1145) 

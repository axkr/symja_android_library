## Module

```
Module({list_of_local_variables}, expr )
```

> evaluates `expr` for the `list_of_local_variables` by renaming local variables.

### Examples

Print `11` to the console and return `10`:

```
>> xm=10;Module({xm=xm}, xm=xm+1;Print(xm));xm
10
```

### Related terms 
[Block](Block.md), [With](With.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Module](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L1463) 

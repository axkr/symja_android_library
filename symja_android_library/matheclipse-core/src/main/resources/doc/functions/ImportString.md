## ImportString

```
ImportString(string, import-format)
```
 
> import the `string` from `import-format`.

**Supported formats:** Base64, ExpressionJSON, JSON, Table
 
### Examples

Import a string from base 64 format: 

```
>> ImportString("SGVsbG8gd29ybGQ=", "Base64")
Hello world
```

### Related terms 
[Export](Export.md), [Import](Import.md), [ExportString](ExportString.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ImportString](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/ImportString.java#L28) 

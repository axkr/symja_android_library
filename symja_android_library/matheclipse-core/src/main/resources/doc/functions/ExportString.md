## ExportString

```
ExportString(string, export-format)
```
 
> export the `string` in `export-format`.
 
**Supported formats:** Base64, ExpressionJSON, JSON, Table

### Examples

Export a string in base 64 format:

```
>> ExportString("Hello world", "Base64")
SGVsbG8gd29ybGQ=

>> ExportString({2.1+I*3.4}, "ExpressionJSON") 
["List",["Complex",2.1,3.4]]
```

### Related terms 
[Export](Export.md), [Import](Import.md), [ImportString](ImportString.md)

 

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ExportString](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/ExportString.java#L36) 

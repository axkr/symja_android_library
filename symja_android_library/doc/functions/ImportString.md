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
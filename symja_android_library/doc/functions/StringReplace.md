## StringReplace

```
StringReplace(string, fromStr -> toStr)
```

> replaces each occurrence of `fromStr` with `toStr` in `string`.

### Examples

```
>> StringReplace("the quick brown fox jumps over the lazy dog", "the" -> "a") 
a quick brown fox jumps over a lazy dog

>> StringReplace("01101100010", "01" .. -> "x")
x1x100x0
```

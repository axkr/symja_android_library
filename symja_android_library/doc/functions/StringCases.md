## StringCases

```
StringCases(str, {"p1", "p2",...})

StringCases(str, "p1" | "p2")
```

> return a list of matches for `"p1", "p2",...` list of strings in the string `str`.
  
### Examples

```
>> StringCases("12341235678", "123" | "78") 
{123,123,78}
```

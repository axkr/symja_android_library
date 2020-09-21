## StringCases

```
StringCases(string, pattern)
```

> gives all occurences of `pattern` in `string`.
  
### Examples

```
>> StringCases("12341235678", "123" | "78") 
{123,123,78}

>> StringCases("a#ä_123", WordCharacter) 
{a,ä,1,2,3}

StringCases("a#ä_123", LetterCharacter)
{a,ä}
```

## LetterCharacter

```
LetterCharacter
```

> represents letters..

### Examples

```
>> StringMatchQ(#, LetterCharacter)& /@ {"a", "1", "A", " ", "."}
{True,False,True,False,False}
```

LetterCharacter also matches unicode characters.

```
>> StringMatchQ("\[Lambda]",LetterCharacter)
```

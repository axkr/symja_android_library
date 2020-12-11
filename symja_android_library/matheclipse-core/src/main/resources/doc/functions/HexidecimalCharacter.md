## HexidecimalCharacter

```
HexidecimalCharacter
```

> represents the characters `0-9`, `a-f` and `A-F`.

### Examples

```
>> StringMatchQ(#,HexidecimalCharacter) & /@ {"a","1","A","x","H"," ","."}
{True,True,True,False,False,False,False}
```
## WordCharacter]

```
WordCharacter]
```

> represents a single letter or digit character.

### Examples

```
>> StringMatchQ(#, WordCharacter) &/@ {"1", "a", "A", ",", " "} 
{True,True,True,False,False}

>> StringMatchQ("abc123DEF", WordCharacter..)
True

>> StringMatchQ("$b;123", WordCharacter..)
False
```
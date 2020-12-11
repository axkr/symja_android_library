## WhitespaceCharacter

```
WhitespaceCharacter
```

> represents a single whitespace character.

### Examples

```
>> StringMatchQ("\n", WhitespaceCharacter)
True

>> StringSplit("a\nb\nc d", WhitespaceCharacter)
{a,b,c,d}
```
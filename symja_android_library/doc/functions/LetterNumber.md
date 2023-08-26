## LetterNumber

```
LetterNumber(character)
```

> returns the position of the `character` in the English alphabet.

```
FromLetterNumber(character, language-string)
```

> returns the position of the `character` in the languages alphabet.
 

### Examples

```
>> LetterNumber("b")
2

>> LetterNumber("B")
2

>> LetterNumber("ij", "Dutch")
26

>> LetterNumber("dzs","Hungarian")
8
```

### Related terms 
[Alphabet](Alphabet.md), [FromLetterNumber](FromLetterNumber.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LetterNumber](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L815) 

## LetterQ

```
LetterQ(expr)
```

> tests whether `expr` is a string, which only contains letters.

A character is considered to be a letter if its general category type, provided by the Java method `Character#getType()` is any of the following:
* `UPPERCASE_LETTER`
* `LOWERCASE_LETTER`
* `TITLECASE_LETTER`
* `MODIFIER_LETTER`
* `OTHER_LETTER`
 
Not all letters have case. Many characters are letters but are neither uppercase nor lowercase nor titlecase.
 
### Examples

```
>> LetterQ("k") 
True

>> LetterQ("7")", //
False 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LetterQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L898) 

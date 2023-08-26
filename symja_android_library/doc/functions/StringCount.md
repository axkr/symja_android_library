## StringCount

```
StringCount(string, pattern)
```

> counts all occurences of `pattern` in `string`.
  
### Examples

```
>> StringCount("a#ä_123", WordCharacter)
5

>> StringCount("a#ä_123", LetterCharacter) 
2 
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringCount](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L1226) 

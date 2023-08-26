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

>> StringCases("a#ä_123", LetterCharacter)
{a,ä}
```

### Related terms
[StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringCases](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L1146) 

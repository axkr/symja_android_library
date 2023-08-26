## StringMatchQ

```
StringMatchQ(string, regex-pattern)
```

> check if the regular expression `regex-pattern` matches the `string`.
  
See
* [Wikipedia - Regular expression](https://en.wikipedia.org/wiki/Regular_expression)

### Examples

```
>> StringMatchQ({"ExpandAll", "listable", "test"}, RegularExpression("li(.+?)le"))
{False,True,False}

>> StringMatchQ("15a94xcZ6", (DigitCharacter | LetterCharacter)..)
True
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringMatchQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L1786) 

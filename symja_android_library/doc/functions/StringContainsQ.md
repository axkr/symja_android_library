## StringContainsQ

```
StringContainsQ(str1, str2)
```

> return a list of matches for `"p1", "p2",...` list of strings in the string `str`.
  
### Examples

```
>> StringContainsQ({"the quick brown fox", "jumps", "over the lazy dog"}, "the")
{True,False,True}
```

### Related terms
[StringCases](StringCases.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringContainsQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L1299) 

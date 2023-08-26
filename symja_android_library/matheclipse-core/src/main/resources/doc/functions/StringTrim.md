## StringTrim

```
StringTrim(s)
```

> returns a version of `s `with whitespace removed from start and end.

```
StringTrim(s, patt)
```

> returns a version of `s` with strings matching `patt` removed from start and end.

### Examples

```
>> StringJoin("a", StringTrim(" \\tb\\n "), "c") 
abc

>> StringTrim("ababaxababyaabab", RegularExpression("(ab)+")) 
axababya
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringTrim](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L2625) 

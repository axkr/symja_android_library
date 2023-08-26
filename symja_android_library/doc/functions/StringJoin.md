## StringJoin

```
StringJoin(str1, str2, ... strN)
```

or

```
str1 <> str2 <>  ... <> strN
```

> returns the concatenation of the strings `str1, str2, ... strN`.

### Examples

```
>> "Java" <> ToString(8)
Java8

>> StringJoin("Java", ToString(8))
Java8

>> StringJoin({"a", "b"})// InputForm
"ab"
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringJoin](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L1665) 

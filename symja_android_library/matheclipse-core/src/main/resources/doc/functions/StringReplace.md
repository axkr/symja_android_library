## StringReplace

```
StringReplace(string, fromStr -> toStr)
```

> replaces each occurrence of `fromStr` with `toStr` in `string`.

### Examples

```
>> StringReplace("the quick brown fox jumps over the lazy dog", "the" -> "a") 
a quick brown fox jumps over a lazy dog

>> StringReplace("01101100010", "01" .. -> "x")
x1x100x0
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)
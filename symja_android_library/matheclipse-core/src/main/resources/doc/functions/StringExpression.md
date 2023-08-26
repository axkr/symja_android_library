## StringExpression

```
StringExpression(s_1, s_2, ...)
```

or

```
s_1 ~~ s_2 ~~ ...
```

> represents a sequence of strings and symbolic string objects `s_i`.
  
### Examples

```
>> "a" ~~ "b" ~~ "c" // FullForm
"abc"
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringExpression](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L1376) 

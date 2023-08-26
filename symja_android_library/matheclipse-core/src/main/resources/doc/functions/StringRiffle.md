## StringRiffle

```
StringRiffle({s1, s2, s3, ...})
```

> returns a new string by concatenating all the `si`, with spaces inserted between them.


```
StringRiffle(list, "sep")
```

> inserts the separator `sep` between all elements in list.

```
StringRiffle(list, {"left", "sep", "right"})
```

> use `left` and `right` as delimiters after concatenation.

### Examples

```
>> StringRiffle({"a", "b", "c", "d", "e"})
a b c d e

>> StringRiffle({"a", "b", "c", "d", "e"}, ", ")
a, b, c, d, e

>> StringRiffle({"a", "b", "c", "d", "e"}, {"(", " ", ")"})
(a b c d e)
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringRiffle](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L2239) 

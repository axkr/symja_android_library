## StringInsert

```
StringInsert(string, new-string, position)
```

> returns a string with `new-string` inserted starting at `position` in `string`.

```
StringInsert(string, new-string, -position)
```

> returns a string with `new-string` inserted starting at `position` from the end of `string`.

```
StringInsert(string, new-string, {pos1, pos2,...})
```

> returns a string with `new-string` inserted at each position `posN` in `string`.

```
StringInsert({str1, strr2,...}, new-string, position)
```

> gives the list of results for each of the strings `strN`

### Examples

```
>> StringInsert({"", "Symja"}, "X", {1, 1, -1}) 
{XXX,XXSymjaX}
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringInsert](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L1566) 

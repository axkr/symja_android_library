## StringPosition

```
StringPosition("string", patt)
```

> gives a list of starting and ending positions where `patt` matches `"string"`.

```
StringPosition("string", patt, n)
```

> returns the first `n` matches only.

```
StringPosition("string", {patt1, patt2,...}, n)
```

> matches multiple patterns.

```
StringPosition({s1, s2, ...}, patt)
```

> returns a list of matches for multiple strings.

See
* [Wikipedia - Regular expression](https://en.wikipedia.org/wiki/Regular_expression)

### Examples

```
>> StringPosition("123ABCxyABCzzzABCABC", "ABC")
{{4,6},{9,11},{15,17},{18,20}}


>> StringPosition("123ABCxyABCzzzABCABC", "ABC", 2)
{{4,6},{9,11}}
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringPosition](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L1911) 

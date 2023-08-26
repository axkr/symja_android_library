## StringTake

```
StringTake("string", n)
```

> gives the first `n` characters in `string`.

```
StringTake("string", -n)
```

> gives the last n characters in `string`.

```
StringTake("string", {n})
```

> gives the `n`th character in `string`.

```
StringTake("string", {m, n})
```

> gives characters `m` through `n` in `string`.

```
StringTake("string", {m, n, s})
```

> gives characters `m` through `n` in steps of `s`.
 
### Examples

```
>> StringTake("abcde", 2)
ab
  
>> StringTake("abcde", 0)

 
>> StringTake("abcde", -2)
de
 
>> StringTake("abcde", {2})
b
   
>> StringTake("abcd", {2,3}]
bc
  
>> StringTake("abcdefgh", {1, 5, 2})
ace
   
>> StringTake("abcdef", All)
abcdef
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringTake](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L2441) 

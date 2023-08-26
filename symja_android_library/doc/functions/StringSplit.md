## StringSplit

```
StringSplit(str)
```

> split the string `str` by whitespaces into a list of strings.

```
StringSplit(str1, str2)
```

> split the string `str1` by `str2` into a list of strings.

```
StringSplit(str1, RegularExpression(str2))
```

> split the string `str1` by the regular expression `str2` into a list of strings.

See
* [Wikipedia - Regular expression](https://en.wikipedia.org/wiki/Regular_expression)

### Examples

```
>> StringSplit("128.0.0.1", ".") 
{128,0,0,1}

>> StringSplit("128.0.0.1", RegularExpression("\\\\W+"))
{128,0,0,1}

>> StringSplit("a1b2.2c0.333d4444.0efghijlkm", NumberString)
{a,b,c,d,efghijlkm}	
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringFreeQ](StringFreeQ.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringSplit](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L2379) 

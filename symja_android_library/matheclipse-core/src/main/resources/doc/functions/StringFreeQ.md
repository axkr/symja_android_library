## StringFreeQ

```
StringFreeQ("string", patt)
```

> returns `True` if no substring in `string` matches the string expression `patt`, and returns `False` otherwise.
 
```
StringFreeQ({"s1", "s2", ...}, patt)
```

> returns the list of results for each element of the string list.

```
StringFreeQ("string", {p1, p2, ...})
```

> returns `True` if no substring matches any of the `pi`.

```
StringFreeQ(patt)
```

> represents an operator form of `StringFreeQ` that can be applied to an expression.

See
* [Wikipedia - Regular expression](https://en.wikipedia.org/wiki/Regular_expression)

### Examples

```
>> StringFreeQ("symja", "s" ~~__ ~~"a")
False

>> StringFreeQ("symja", "y" ~~__~~"s")
True

>> StringFreeQ("Symja", "SY" ,IgnoreCase -> True)", //
False

>> StringFreeQ({"g", "a", "laxy", "universe", "sun"}, "u") 
{True,True,True,False,False}

>> StringFreeQ("e" ~~___ ~~"u") /@ {"The Sun", "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"}
{False,False,False,True,True,True,True,True,False}
        
>> StringFreeQ({"A", "Galaxy", "Far", "Far", "Away"}, {"F" ~~__ ~~"r", "aw" ~~___}, IgnoreCase ->True)
{True,True,False,False,False}
```

### Related terms
[StringCases](StringCases.md), [StringContainsQ](StringContainsQ.md), [StringCount](StringCount.md), [StringExpression](StringExpression.md), [StringInsert](StringInsert.md), [StringJoin](StringJoin.md), [StringLength](StringLength.md), [StringMatchQ](StringMatchQ.md), [StringPart](StringPart.md), [StringPosition](StringPosition.md), [StringQ](StringQ.md), [StringReplace](StringReplace.md), [StringRiffle](StringRiffle.md), [StringSplit](StringSplit.md), [StringTake](StringTake.md), [StringToByteArray](StringToByteArray.md), [StringTrim](StringTrim.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringFreeQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L1443) 

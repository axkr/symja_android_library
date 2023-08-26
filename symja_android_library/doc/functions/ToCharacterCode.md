## ToCharacterCode

```
ToCharacterCode(string)
```

> converts `string` into a list of corresponding integer character codes.

 
### Examples

```
>> ToCharacterCode("ABCD abcd")
{65,66,67,68,32,97,98,99,100}

>> "a-3" // ToCharacterCode
{97,45,51}
```

### Related terms 
[FromCharacterCode](FromCharacterCode.md), [ToUnicode](ToUnicode.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ToCharacterCode](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L2918) 

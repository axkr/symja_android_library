## BinaryDeserialize

```
BinaryDeserialize(byte-array)
```

> deserialize the `byte-array` from WXF format into a Symja expression.
 
### Examples

```
>> BinaryDeserialize(ByteArray({56,58,102,2,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103,67,2})) 
f(g,2) 
```

### Related terms 
[BinarySerialize](BinarySerialize.md), [ByteArray](ByteArray.md), [ByteArrayQ](ByteArrayQ.md), [Export](Export.md), [Import](Import.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BinaryDeserialize](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/WXFFunctions.java#L99) 

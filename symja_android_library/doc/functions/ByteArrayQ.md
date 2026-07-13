## ByteArrayQ

```
ByteArrayQ(expr)
```

> returns `True` if `expr` is a `ByteArray` object, and `False` otherwise.
 
### Examples

```
>> ByteArrayQ(ByteArray({1,2,3}))
True

>> ByteArrayQ(Range(3))
False
```


```
>> ByteArrayQ(ByteArray("xyz"))
: The argument at position 1 in ByteArray(xyz) should be a vector of unsigned byte values or a Base64-encoded string.
True
```


### Related terms 
[BinaryDeserialize](BinaryDeserialize.md), [BinarySerialize](BinarySerialize.md), [ByteArray](ByteArray.md), [Export](Export.md), [Import](Import.md), [Normal](Normal.md)
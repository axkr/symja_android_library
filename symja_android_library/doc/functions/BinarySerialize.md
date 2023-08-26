## BinarySerialize

```
BinarySerialize(expr)
```

> serialize the Symja `expr` into a byte array expression in WXF format.
 
### Examples

```
>> BinarySerialize(f(g,2)) // Normal
{56,58,102,2,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103,67,2}
```


### Related terms 
[BinaryDeserialize](BinaryDeserialize.md), [ByteArray](ByteArray.md), [ByteArrayQ](ByteArrayQ.md), [Export](Export.md), [Import](Import.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BinarySerialize](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/WXFFunctions.java#L33) 

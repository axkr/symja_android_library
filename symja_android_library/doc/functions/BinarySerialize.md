## BinarySerialize

```
BinarySerialize(expr)
```

> serialize the Symja `expr` into a byte array expression in WXF format.

```
BinarySerialize(expr, PerformanceGoal -> "Size")
```

> serialize the Symja `expr` into compressed WXF: the header `8C:` followed by the zlib-compressed body.
 
### Examples

```
>> BinarySerialize(f(g,2)) // Normal
{56,58,102,2,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103,67,2}

>> Take(Normal(BinarySerialize(f(g,2), PerformanceGoal -> "Size")), 3)
{56,67,58}
```


### Related terms 
[BinaryDeserialize](BinaryDeserialize.md), [ByteArray](ByteArray.md), [ByteArrayQ](ByteArrayQ.md), [Export](Export.md), [Import](Import.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BinarySerialize](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/WXFFunctions.java#L33) 

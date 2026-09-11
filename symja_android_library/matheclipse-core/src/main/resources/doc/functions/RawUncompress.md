## RawUncompress

```
Developer`RawUncompress(bytes)
```

> inflates zlib-compressed bytes - a list of byte values or a `ByteArray` - and gives the original bytes in the same form. Bytes which are not a zlib stream are left unevaluated.

### Examples

```
>> Developer`RawUncompress(Developer`RawCompress({1,2,3,250}))
{1,2,3,250}
```

### Related terms 
[RawCompress](RawCompress.md), [Uncompress](Uncompress.md), [ByteArray](ByteArray.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RawUncompress](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/WXFFunctions.java)

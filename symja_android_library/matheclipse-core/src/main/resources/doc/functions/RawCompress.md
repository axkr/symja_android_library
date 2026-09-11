## RawCompress

```
Developer`RawCompress(bytes)
```

> compresses a list of byte values, or a `ByteArray`, with zlib and gives the compressed bytes in the same form.

The WLJS notebook sends large objects - plots - to the browser as `BaseEncode(ByteArray(Developer`RawCompress(bytes)))`.

### Examples

```
>> First(Developer`RawCompress({1,2,3}))
120

>> Developer`RawUncompress(Developer`RawCompress({1,2,3,250}))
{1,2,3,250}
```

### Related terms 
[RawUncompress](RawUncompress.md), [Compress](Compress.md), [ByteArray](ByteArray.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RawCompress](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/WXFFunctions.java)

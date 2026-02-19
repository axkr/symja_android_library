## Uncompress

```
Uncompress(string)
```

> an expression compressed by the `Compress` function can be fully reconstructed using the `Uncompress` function.

### Examples

```
>> str = Compress(Expand((a+b)^3)) 
H4sIAAAAAAAA/0uMM1bQVjDWSowz0kqCsLSS4oyArKQ4YwDZmlsNHQAAAA== 

>> Uncompress(str) 
a^3+3*a^2*b+3*a*b^2+b^3
```

### Related terms 
[Compress](Compress.md) 

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Uncompress](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FileFunctions.java#L1596) 

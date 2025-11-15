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
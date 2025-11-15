## Compress

```
Compress(expression)
```

> the `Compress` function creates a compressed, string-based representation of any expression. The output string contains the compressed data of the serialized expression. This string can be stored or transmitted, and the original expression can be fully reconstructed using the `Uncompress` function.

### Examples

```
>> str = Compress(Expand((a+b)^3)) 
H4sIAAAAAAAA/0uMM1bQVjDWSowz0kqCsLSS4oyArKQ4YwDZmlsNHQAAAA== 

>> Uncompress(str) 
a^3+3*a^2*b+3*a*b^2+b^3
```

### Related terms 
[Uncompress](Uncompress.md) 
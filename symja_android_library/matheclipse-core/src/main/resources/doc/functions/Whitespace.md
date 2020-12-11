## Whitespace

```
Whitespace
```

> represents a sequence of whitespace characters.

### Examples

```
>> StringMatchQ(\"\r \n", Whitespace)
True

>> StringSplit("a \n b \r\n c d", Whitespace)
{a,b,c,d}
```
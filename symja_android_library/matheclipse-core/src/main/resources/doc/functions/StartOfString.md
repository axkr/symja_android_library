## StartOfString

```
StartOfString
```

> represents the start of a string.

### Examples 

Test whether strings start with `“a”`:

```
>> StringMatchQ(#, StartOfString ~~"a" ~~__) &/@ {"apple", "banana", "artichoke"}
{True,False,True}
```

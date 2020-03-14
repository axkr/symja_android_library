## StringSplit

```
StringSplit(str1, str2)
```

> split the string `str1` by `str2` into a list of strings.

```
StringSplit(str1, RegularExpression(str2))
```

> split the string `str1` by the regular expression `str2` into a list of strings.

### Examples

```
>> StringSplit("128.0.0.1", ".") 
{128,0,0,1}

>> StringSplit("128.0.0.1", RegularExpression("\\W+"))
{128,0,0,1}				
```

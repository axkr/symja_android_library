## StringMatchQ

```
StringMatchQ(str, RegularExpression(pattern-string))
```

> check if the regular expression `pattern-string` matches the string `str`.
  
See
* [Wikipedia - Regular expression](https://en.wikipedia.org/wiki/Regular_expression)

### Examples

```
>> StringMatchQ({"ExpandAll", "listable", "test"}, RegularExpression("li(.+?)le"))
{False,True,False}
```

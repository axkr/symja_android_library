## StringMatchQ

```
StringMatchQ(string, regex-pattern)
```

> check if the regular expression `regex-pattern` matches the `string`.
  
See
* [Wikipedia - Regular expression](https://en.wikipedia.org/wiki/Regular_expression)

### Examples

```
>> StringMatchQ({"ExpandAll", "listable", "test"}, RegularExpression("li(.+?)le"))
{False,True,False}

>> StringMatchQ("15a94xcZ6", (DigitCharacter | LetterCharacter)..)
True
```

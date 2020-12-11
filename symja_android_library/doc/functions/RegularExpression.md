## RegularExpression

```
RegularExpression("regex")

```

> represents the regular expression specified by the string `“regex”`.

### Examples

```
>> StringSplit("1.23, 4.56 7.89",RegularExpression("(\\s|,)+"))
{1.23,4.56,7.89}
```

## StringJoin

```
StringJoin(str1, str2, ... strN)
```

or

```
str1 <> str2 <>  ... <> strN
```

> returns the concatenation of the strings `str1, str2, ... strN`.

### Examples

```
>> "Java" <> ToString(8)
Java8

>> StringJoin("Java", ToString(8))
Java8

>> StringJoin({"a", "b"})// InputForm
"ab"
```

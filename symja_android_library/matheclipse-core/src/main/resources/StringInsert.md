## StringInsert

```
StringInsert(string, new-string, position)
```

> returns a string with `new-string` inserted starting at `position` in `string`.

```
StringInsert(string, new-string, -position)
```

> returns a string with `new-string` inserted starting at `position` from the end of `string`.

```
StringInsert(string, new-string, {pos1, pos2,...})
```

> returns a string with `new-string` inserted at each position `posN` in `string`.

```
StringInsert({str1, strr2,...}, new-string, position)
```

> gives the list of results for each of the strings `strN`

### Examples

```
>> StringInsert({"", "Symja"}, "X", {1, 1, -1}) 
{XXX,XXSymjaX}
```

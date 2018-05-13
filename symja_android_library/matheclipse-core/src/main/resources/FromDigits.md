## FromDigits

```
FromDigits(list)
```

> creates an expression from the list of digits for radix `10`.

```
FromDigits(list, radix)
```

> creates an expression from the list of digits for the given `radix`.

```
FromDigits(string)
```

> creates an expression from the characters in the `string` for radix `10`.

```
FromDigits(string, radix)
```

> creates an expression from the characters in the `string` for radix `10`.

### Examples

```
>> FromDigits("789abc")
790122

>> FromDigits("789abc", 16)
790122

>> FromDigits({1,1,1,1,0,1,1}, 2)
123
```
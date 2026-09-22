## SystemOptions

```
SystemOptions("name")
```

> gives the settings of the internal system option `name`.

Only `"DifferentiationOptions"` is answered here, with the functions `D` does not differentiate
through. Any other name gives an empty list.

See
* [Wolfram Documentation - SystemOptions](https://reference.wolfram.com/language/ref/SystemOptions.html)

### Examples

```
>> SystemOptions("DifferentiationOptions")
{DifferentiationOptions->{ExcludedFunctions->{Hold,HoldComplete,Less,LessEqual,Greater,GreaterEqual,Inequality,Unequal,Nand,Nor,Xor,Not,Element,Exists,ForAll,Implies,Positive,Negative,NonPositive,NonNegative,Replace,ReplaceAll,ReplaceRepeated}}}
```

### Related terms
[SetSystemOptions](SetSystemOptions.md)

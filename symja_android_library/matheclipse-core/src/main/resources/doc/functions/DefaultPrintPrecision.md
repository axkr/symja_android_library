## DefaultPrintPrecision

```
DefaultPrintPrecision -> n
```

> is an option of `NumberForm`, `PaddedForm` and `DecimalForm` which prints real numbers with `n` significant digits, if no number of digits is given to the wrapper itself.

The default `DefaultPrintPrecision -> Automatic` uses the precision of the number.

### Examples

```
>> NumberForm(1.23456789, DefaultPrintPrecision -> 3)
1.23
```

### Related terms
[DecimalForm](DecimalForm.md), [NumberForm](NumberForm.md), [PaddedForm](PaddedForm.md)

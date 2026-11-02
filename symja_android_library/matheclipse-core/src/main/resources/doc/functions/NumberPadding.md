## NumberPadding

```
NumberPadding -> {"left", "right"}
```

> is an option of the number form wrappers which sets the characters used to pad a number on the left and on the right to the requested number of digits.

The default is `NumberPadding -> {"", "0"}` for `NumberForm` and `DecimalForm`, and `NumberPadding -> {" ", "0"}` for `PaddedForm`.

### Examples

```
>> NumberForm(12.3, {8, 3}, NumberPadding -> {"*", "0"})
****12.300

>> PaddedForm(123, 6, NumberPadding -> "0")
0000123
```

### Related terms
[AccountingForm](AccountingForm.md), [DecimalForm](DecimalForm.md), [EngineeringForm](EngineeringForm.md), [NumberForm](NumberForm.md), [PaddedForm](PaddedForm.md), [ScientificForm](ScientificForm.md), [SignPadding](SignPadding.md)

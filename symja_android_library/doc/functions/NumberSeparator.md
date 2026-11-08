## NumberSeparator

```
NumberSeparator -> "sep"
```

> is an option of the number form wrappers which sets the string between the digit blocks.

```
NumberSeparator -> {"sepl", "sepr"}
```

> uses `"sepl"` to the left and `"sepr"` to the right of the decimal point.

The digits are only separated into blocks if the option `DigitBlock` is set. The default is `NumberSeparator -> {",", ""}`.

### Examples

```
>> NumberForm(1234567, DigitBlock -> 3, NumberSeparator -> ".")
1.234.567

>> NumberForm(1234.5678, {8, 4}, DigitBlock -> 3, NumberSeparator -> {"'", "_"})
1'234.567_8
```

### Related terms
[AccountingForm](AccountingForm.md), [DecimalForm](DecimalForm.md), [DigitBlock](DigitBlock.md), [EngineeringForm](EngineeringForm.md), [NumberForm](NumberForm.md), [PaddedForm](PaddedForm.md), [ScientificForm](ScientificForm.md)

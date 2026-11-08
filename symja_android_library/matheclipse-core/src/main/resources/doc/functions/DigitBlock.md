## DigitBlock

```
DigitBlock -> n
```

> is an option of the number form wrappers which separates the digits into blocks of `n` digits.

```
DigitBlock -> {nl, nr}
```

> uses blocks of `nl` digits to the left and `nr` digits to the right of the decimal point.

The default `DigitBlock -> Infinity` does not separate the digits. The separator is set by the option `NumberSeparator`.

### Examples

```
>> NumberForm(1234567, DigitBlock -> 3)
1,234,567

>> AccountingForm(-1234567, DigitBlock -> 3)
(1,234,567)

>> NumberForm(1234.5678, {8, 4}, DigitBlock -> 3, NumberSeparator -> {"'", "_"})
1'234.567_8
```

### Related terms
[AccountingForm](AccountingForm.md), [DecimalForm](DecimalForm.md), [EngineeringForm](EngineeringForm.md), [NumberForm](NumberForm.md), [NumberSeparator](NumberSeparator.md), [PaddedForm](PaddedForm.md), [ScientificForm](ScientificForm.md)

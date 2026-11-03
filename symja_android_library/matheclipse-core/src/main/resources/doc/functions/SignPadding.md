## SignPadding

```
SignPadding -> True
```

> is an option of the number form wrappers which puts the sign of a number in front of the padding instead of directly in front of the digits.

The default is `SignPadding -> False`.

### Examples

```
>> PaddedForm(-12.3, {8, 2})
    -12.30

>> PaddedForm(-12.3, {8, 2}, SignPadding -> True)
-    12.30
```

### Related terms
[AccountingForm](AccountingForm.md), [DecimalForm](DecimalForm.md), [EngineeringForm](EngineeringForm.md), [NumberForm](NumberForm.md), [NumberPadding](NumberPadding.md), [NumberSigns](NumberSigns.md), [PaddedForm](PaddedForm.md), [ScientificForm](ScientificForm.md)

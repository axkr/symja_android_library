## NumberPoint

```
NumberPoint -> "point"
```

> is an option of the number form wrappers which sets the string used as the decimal point.

The default is `NumberPoint -> "."`.

### Examples

```
>> NumberForm(1234.5, NumberPoint -> ",")
1234,5

>> ScientificForm(2.5*^7, NumberPoint -> ",")
2,5×10^7
```

### Related terms
[AccountingForm](AccountingForm.md), [DecimalForm](DecimalForm.md), [EngineeringForm](EngineeringForm.md), [NumberForm](NumberForm.md), [NumberSeparator](NumberSeparator.md), [PaddedForm](PaddedForm.md), [ScientificForm](ScientificForm.md)

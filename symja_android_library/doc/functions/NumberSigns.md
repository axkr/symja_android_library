## NumberSigns

```
NumberSigns -> {"neg", "pos"}
```

> is an option of the number form wrappers which sets the strings put in front of negative and of positive numbers.

```
NumberSigns -> {{"negl", "negr"}, {"posl", "posr"}}
```

> sets the strings put on the left and on the right of negative and of positive numbers.

The default is `NumberSigns -> {"-", ""}`. `AccountingForm` puts negative numbers in parentheses.

### Examples

```
>> NumberForm(-1234.5, {6, 1}, NumberSigns -> {"n", "p"})
n1234.5

>> PaddedForm(123, 6, NumberSigns -> {"-", "+"})
   +123
```

### Related terms
[AccountingForm](AccountingForm.md), [DecimalForm](DecimalForm.md), [EngineeringForm](EngineeringForm.md), [NumberForm](NumberForm.md), [PaddedForm](PaddedForm.md), [ScientificForm](ScientificForm.md), [SignPadding](SignPadding.md)

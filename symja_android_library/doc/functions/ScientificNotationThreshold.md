## ScientificNotationThreshold

```
ScientificNotationThreshold -> {min, max}
```

> is an option of `PaddedForm` which prints a number in scientific notation if its exponent is below `min` or at least `max`.

The default is `ScientificNotationThreshold -> {-5, 6}`.

### Examples

```
>> PaddedForm(1.0*^12)
1.×10^12

>> PaddedForm(1.0*^12, ScientificNotationThreshold -> {-15, 15})
1000000000000.
```

### Related terms
[NumberForm](NumberForm.md), [PaddedForm](PaddedForm.md), [ScientificForm](ScientificForm.md)

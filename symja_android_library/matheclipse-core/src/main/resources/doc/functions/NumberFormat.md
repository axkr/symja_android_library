## NumberFormat

```
NumberFormat -> f
```

> is an option of the number form wrappers which prints a number as `f(mantissa, base, exponent)`, where the mantissa, the base and the exponent are given as strings.

The exponent is the empty string `""` if the number is not printed in scientific notation. The default `NumberFormat -> Automatic` writes `mantissa×base^exponent`.

### Examples

```
>> ScientificForm(2.5*^7, NumberFormat -> (#1 <> "e" <> #3 &))
2.5e7
```

### Related terms
[EngineeringForm](EngineeringForm.md), [NumberForm](NumberForm.md), [NumberMultiplier](NumberMultiplier.md), [ScientificForm](ScientificForm.md)

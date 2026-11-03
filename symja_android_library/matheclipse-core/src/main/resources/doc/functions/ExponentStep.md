## ExponentStep

```
ExponentStep -> s
```

> is an option of the number form wrappers which makes the exponent of scientific notation a multiple of `s`.

The default is `ExponentStep -> 1`. `EngineeringForm` uses `ExponentStep -> 3`.

### Examples

```
>> ScientificForm(1.234*^7, ExponentStep -> 3)
12.34×10^6

>> ExponentStep /. Options(EngineeringForm)
3
```

### Related terms
[EngineeringForm](EngineeringForm.md), [ExponentFunction](ExponentFunction.md), [NumberForm](NumberForm.md), [ScientificForm](ScientificForm.md)

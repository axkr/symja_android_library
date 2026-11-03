## EngineeringForm

```
EngineeringForm(expr)
```

> prints the real numbers in `expr` in engineering notation, in which the exponent is a multiple of `3`.

```
EngineeringForm(expr, n)
```

> prints the real numbers in `expr` in engineering notation with `n` significant digits.

`EngineeringForm` is `ScientificForm` with the option `ExponentStep -> 3`. The wrapper only changes how the numbers in `expr` are printed. It is not evaluated away, so the result should not be used in further calculations. The options are `DigitBlock`, `ExponentFunction`, `ExponentStep`, `NumberFormat`, `NumberMultiplier`, `NumberPadding`, `NumberPoint`, `NumberSeparator`, `NumberSigns` and `SignPadding`.

### Examples

```
>> EngineeringForm(25000000.0)
25.×10^6

>> EngineeringForm(4.7*^-5)
47.×10^-6

>> EngineeringForm(1.0*^-7)
100.×10^-9

>> EngineeringForm(2.5)
2.5
```

```
>> EngineeringForm(1.23456*^8, 3)
123.×10^6
```

### Related terms
[AccountingForm](AccountingForm.md), [DecimalForm](DecimalForm.md), [ExponentStep](ExponentStep.md), [NumberForm](NumberForm.md), [PaddedForm](PaddedForm.md), [ScientificForm](ScientificForm.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of EngineeringForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/form/NumberFormatter.java)

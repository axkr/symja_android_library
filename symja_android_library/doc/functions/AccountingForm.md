## AccountingForm

```
AccountingForm(expr)
```

> prints the numbers in `expr` in accounting notation: negative numbers are put in parentheses, and scientific notation is never used.

```
AccountingForm(expr, n)
```

> prints the real numbers in `expr` with `n` significant digits.

```
AccountingForm(expr, {n, f})
```

> prints the real numbers in `expr` with `n` digits, `f` of them to the right of the decimal point.

The wrapper only changes how the numbers in `expr` are printed. It is not evaluated away, so the result should not be used in further calculations. The options are `DigitBlock`, `ExponentFunction`, `ExponentStep`, `NumberFormat`, `NumberMultiplier`, `NumberPadding`, `NumberPoint`, `NumberSeparator`, `NumberSigns` and `SignPadding`.

### Examples

```
>> AccountingForm(-2.5)
(2.5)

>> AccountingForm({-1.5, 2.5})
{(1.5),2.5}

>> AccountingForm(1.0*^12)
1000000000000.

>> AccountingForm(12.345, {5, 2})
12.35
```

```
>> AccountingForm(-1234567, DigitBlock -> 3)
(1,234,567)
```

### Related terms
[DecimalForm](DecimalForm.md), [DigitBlock](DigitBlock.md), [EngineeringForm](EngineeringForm.md), [NumberForm](NumberForm.md), [NumberSigns](NumberSigns.md), [PaddedForm](PaddedForm.md), [ScientificForm](ScientificForm.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of AccountingForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/form/NumberFormatter.java)

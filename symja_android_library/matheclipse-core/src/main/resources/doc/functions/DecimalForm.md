## DecimalForm

```
DecimalForm(expr)
```

> prints the real numbers in `expr` in decimal notation, never in scientific notation.

```
DecimalForm(expr, n)
```

> prints the real numbers in `expr` in decimal notation with `n` significant digits.

```
DecimalForm(expr, {n, f})
```

> prints the real numbers in `expr` with `n` digits, `f` of them to the right of the decimal point.

The wrapper only changes how the numbers in `expr` are printed. It is not evaluated away, so the result should not be used in further calculations. The options are `DefaultPrintPrecision`, `DigitBlock`, `NumberPadding`, `NumberPoint`, `NumberSeparator`, `NumberSigns` and `SignPadding`.

### Examples

```
>> DecimalForm(1.0*^12)
1000000000000.

>> DecimalForm(1.0*^-6)
0.000001

>> DecimalForm(N(Pi), 4)
3.142
```

```
>> DecimalForm(1234567.0, DigitBlock -> 3)
1,234,567.
```

### Related terms
[AccountingForm](AccountingForm.md), [DigitBlock](DigitBlock.md), [EngineeringForm](EngineeringForm.md), [NumberForm](NumberForm.md), [PaddedForm](PaddedForm.md), [ScientificForm](ScientificForm.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DecimalForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/form/NumberFormatter.java)

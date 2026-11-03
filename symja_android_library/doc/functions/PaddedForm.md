## PaddedForm

```
PaddedForm(expr, n)
```

> prints the numbers in `expr` padded on the left to a field of `n` digits.

```
PaddedForm(expr, {n, f})
```

> prints the numbers in `expr` with `n` digits, `f` of them to the right of the decimal point, padded to a field of that width.

```
PaddedForm(expr)
```

> prints the numbers in `expr` with the default number of digits.

The field has one more position for the sign, so that a column of positive and negative numbers lines up. Numbers outside of the `ScientificNotationThreshold` are printed in scientific notation. The wrapper only changes how the numbers in `expr` are printed. It is not evaluated away, so the result should not be used in further calculations. The options are `DefaultPrintPrecision`, `DigitBlock`, `ExponentFunction`, `ExponentStep`, `NumberFormat`, `NumberMultiplier`, `NumberPadding`, `NumberPoint`, `NumberSeparator`, `NumberSigns`, `ScientificNotationThreshold` and `SignPadding`.

### Examples

```
>> PaddedForm(123, 6)
    123

>> PaddedForm(12.3, {6, 2})
   12.30

>> PaddedForm(-12.3, {6, 2})
  -12.30
```

Pad with zeros, or put the sign in front of the padding:

```
>> PaddedForm(123, 6, NumberPadding -> "0")
0000123

>> PaddedForm(-12.3, {8, 2}, SignPadding -> True)
-    12.30
```

A list of numbers:

```
>> PaddedForm({1.5, -22.25, 333.5}, {5, 2})
{   1.50, -22.25, 333.50}
```

```
>> PaddedForm(123456, 8, DigitBlock -> 3)
   123,456
```

### Related terms
[AccountingForm](AccountingForm.md), [DecimalForm](DecimalForm.md), [EngineeringForm](EngineeringForm.md), [NumberForm](NumberForm.md), [NumberPadding](NumberPadding.md), [ScientificForm](ScientificForm.md), [ScientificNotationThreshold](ScientificNotationThreshold.md), [SignPadding](SignPadding.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of PaddedForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/form/NumberFormatter.java)

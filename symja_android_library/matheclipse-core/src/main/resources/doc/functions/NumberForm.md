## NumberForm

```
NumberForm(expr, n)
```

> prints the real numbers in `expr` with `n` significant digits.

```
NumberForm(expr, {n, f})
```

> prints the real numbers in `expr` with `n` digits, `f` of them to the right of the decimal point.

```
NumberForm(expr)
```

> prints the real numbers in `expr` with the default number of digits.

Numbers whose exponent is above `5` or below `-5` are printed in scientific notation. Exact integers are never printed in scientific notation. The wrapper only changes how the numbers in `expr` are printed. It is not evaluated away, so the result should not be used in further calculations. The options are `DefaultPrintPrecision`, `DigitBlock`, `ExponentFunction`, `ExponentStep`, `NumberFormat`, `NumberMultiplier`, `NumberPadding`, `NumberPoint`, `NumberSeparator`, `NumberSigns` and `SignPadding`.

### Examples

```
>> NumberForm(N(Pi, 20), 10)
3.141592654

>> NumberForm(3.14159, {5, 2})
3.14

>> NumberForm(1.5, {5, 3})
1.500
```

Scientific notation for large numbers:

```
>> NumberForm(123456.7)
123456.7

>> NumberForm(1234567.8)
1.234568×10^6
```

Group the digits of an integer:

```
>> NumberForm(1234567, DigitBlock -> 3)
1,234,567

>> NumberForm(1234567, DigitBlock -> 3, NumberSeparator -> ".")
1.234.567
```

The numbers inside an expression are formatted too:

```
>> NumberForm(x + 2.5*y, 3)
x+2.5*y
```

### Related terms
[AccountingForm](AccountingForm.md), [DecimalForm](DecimalForm.md), [DigitBlock](DigitBlock.md), [EngineeringForm](EngineeringForm.md), [NumberPadding](NumberPadding.md), [NumberPoint](NumberPoint.md), [PaddedForm](PaddedForm.md), [ScientificForm](ScientificForm.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of NumberForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/form/NumberFormatter.java)

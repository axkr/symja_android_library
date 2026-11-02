## ScientificForm

```
ScientificForm(expr)
```

> prints the real numbers in `expr` in scientific notation, with one digit before the decimal point.

```
ScientificForm(expr, n)
```

> prints the real numbers in `expr` in scientific notation with `n` significant digits.

```
ScientificForm(expr, {n, f})
```

> prints the real numbers in `expr` with `n` digits, `f` of them to the right of the decimal point.

A number whose exponent is `0` is printed without the factor `10^0`. Exact integers and fractions are not printed in scientific notation. The wrapper only changes how the numbers in `expr` are printed. It is not evaluated away, so the result should not be used in further calculations. The options are `DigitBlock`, `ExponentFunction`, `ExponentStep`, `NumberFormat`, `NumberMultiplier`, `NumberPadding`, `NumberPoint`, `NumberSeparator`, `NumberSigns` and `SignPadding`.

### Examples

```
>> ScientificForm(25000000.0)
2.5×10^7

>> ScientificForm(0.000025)
2.5×10^-5

>> ScientificForm(2.5)
2.5

>> ScientificForm(1.23456789, 4)
1.235
```

```
>> ScientificForm({1.0*^7, 2.0*^-8})
{1.×10^7,2.×10^-8}
```

Exact integers are not changed:

```
>> ScientificForm(123456)
123456
```

A custom layout of the mantissa and the exponent:

```
>> ScientificForm(2.5*^7, NumberFormat -> (#1 <> "e" <> #3 &))
2.5e7
```

```
>> TeXForm(ScientificForm(25000000.0))
2.5\times {10}^{7}
```

### Related terms
[AccountingForm](AccountingForm.md), [DecimalForm](DecimalForm.md), [EngineeringForm](EngineeringForm.md), [ExponentFunction](ExponentFunction.md), [ExponentStep](ExponentStep.md), [NumberForm](NumberForm.md), [NumberFormat](NumberFormat.md), [NumberMultiplier](NumberMultiplier.md), [PaddedForm](PaddedForm.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ScientificForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/form/NumberFormatter.java)

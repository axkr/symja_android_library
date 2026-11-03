## ExponentFunction

```
ExponentFunction -> f
```

> is an option of the number form wrappers which applies `f` to the exponent a number would be printed with in scientific notation. `f` returns the exponent to use, or `Null` to print the number without scientific notation.

The default `ExponentFunction -> Automatic` uses the rules of the respective form.

### Examples

Always write the exponent:

```
>> ScientificForm(2.5, ExponentFunction -> (# &))
2.5×10^0
```

Never use scientific notation:

```
>> ScientificForm(1.0*^7, ExponentFunction -> (Null &))
10000000.
```

### Related terms
[EngineeringForm](EngineeringForm.md), [ExponentStep](ExponentStep.md), [NumberForm](NumberForm.md), [ScientificForm](ScientificForm.md)

## SpecialsFreeQ

```
SpecialsFreeQ(expr)
```

> returns `True` if `expr` does not contain the symbols `DirectedInfinity` or `Indeterminate`.

### Examples

```
>> SpecialsFreeQ(-Infinity)
False

>> SpecialsFreeQ(1+x^2)
True
```

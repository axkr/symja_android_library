## Symbol

```
Symbol
```

> is the head of symbols.

### Examples

```
>> Head(x)
Symbol
```

You can use `Symbol` to create symbols from strings:

```
>> Symbol("x") + Symbol("x")
2*x

>> {\[Eta], \[CapitalGamma]\[Beta], Z\[Infinity], \[Angle]XYZ, \[FilledSquare]r, i\[Ellipsis]j}
{\u03b7, \u0393\u03b2, Z\u221e, \u2220XYZ, \u25a0r, i\u2026j}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Symbol](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L2067) 

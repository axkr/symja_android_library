## MathieuCharacteristicA

```
MathieuCharacteristicA(r, q)
```

> returns the characteristic value `a` for which the Mathieu equation `y''(z) + (a - 2*q*Cos(2*z))*y(z) == 0` has an even solution `Exp(I*r*z)*p(z)` with a `2*Pi` periodic `p(z)`.

For an integer `r == n` this is the classical characteristic value `a_n(q)` of the even periodic Mathieu function `ce_n`. For a non-integer `r` it is the eigenvalue continuously connected to `r^2` at `q == 0`, and `MathieuCharacteristicB` gives the same value.

See
* [Wikipedia - Mathieu function](https://en.wikipedia.org/wiki/Mathieu_function)
* [NIST Digital Library of Mathematical Functions - Mathieu Functions](https://dlmf.nist.gov/28)

### Examples

```
>> MathieuCharacteristicA({0,1,2}, 1.0)
{-0.455139,1.85911,4.3713}

>> N(MathieuCharacteristicA(3/2, 1), 40)
2.537180087119901695599980222737527197543

>> MathieuCharacteristicA(r, 0)
r^2
```

### Related terms
[MathieuC](MathieuC.md), [MathieuCharacteristicB](MathieuCharacteristicB.md), [MathieuCharacteristicExponent](MathieuCharacteristicExponent.md), [MathieuCPrime](MathieuCPrime.md), [MathieuS](MathieuS.md), [MathieuSPrime](MathieuSPrime.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of MathieuCharacteristicA](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java)

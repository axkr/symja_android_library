## MathieuCharacteristicB

```
MathieuCharacteristicB(r, q)
```

> returns the characteristic value `b` for which the Mathieu equation `y''(z) + (a - 2*q*Cos(2*z))*y(z) == 0` has an odd solution `Exp(I*r*z)*p(z)` with a `2*Pi` periodic `p(z)`.

For an integer `r == n > 0` this is the classical characteristic value `b_n(q)` of the odd periodic Mathieu function `se_n`; `MathieuCharacteristicB(0, q)` is not defined. For a non-integer `r` it gives the same value as `MathieuCharacteristicA`.

See
* [Wikipedia - Mathieu function](https://en.wikipedia.org/wiki/Mathieu_function)
* [NIST Digital Library of Mathematical Functions - Mathieu Functions](https://dlmf.nist.gov/28)

### Examples

```
>> MathieuCharacteristicB({1,2}, 1.0)
{-0.110249,3.91702}

>> MathieuCharacteristicB(1.5, 1.0) - MathieuCharacteristicA(1.5, 1.0)
0.0

>> MathieuCharacteristicB(r, 0)
r^2
```

### Related terms
[MathieuC](MathieuC.md), [MathieuCharacteristicA](MathieuCharacteristicA.md), [MathieuCharacteristicExponent](MathieuCharacteristicExponent.md), [MathieuCPrime](MathieuCPrime.md), [MathieuS](MathieuS.md), [MathieuSPrime](MathieuSPrime.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of MathieuCharacteristicB](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java)

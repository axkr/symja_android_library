## MathieuCharacteristicExponent

```
MathieuCharacteristicExponent(a, q)
```

> returns the characteristic exponent `r` for which the Mathieu equation `y''(z) + (a - 2*q*Cos(2*z))*y(z) == 0` has a solution `Exp(I*r*z)*p(z)` with a `2*Pi` periodic `p(z)`.

For real `a` and `q` the exponent lies on the branch on which `a` increases with `r`, continuous from `Sqrt(a)` at `q == 0`: it is real in `(m, m+1)` when `a` lies in the `m`-th stability band, and `m + I*mu` with `mu > 0` in the instability gap above it. So `MathieuCharacteristicA(MathieuCharacteristicExponent(a, q), q) == a` in every stability band.

See
* [Wikipedia - Mathieu function](https://en.wikipedia.org/wiki/Mathieu_function)
* [NIST Digital Library of Mathematical Functions - Mathieu Functions](https://dlmf.nist.gov/28)

### Examples

```
>> MathieuCharacteristicExponent(2, 0.5)
1.36951

>> MathieuCharacteristicExponent(1.0, 1.0)
1.0+I*0.453454

>> MathieuCharacteristicExponent(a, 0)
Sqrt(a)
```

### Related terms
[MathieuC](MathieuC.md), [MathieuCharacteristicA](MathieuCharacteristicA.md), [MathieuCharacteristicB](MathieuCharacteristicB.md), [MathieuCPrime](MathieuCPrime.md), [MathieuS](MathieuS.md), [MathieuSPrime](MathieuSPrime.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of MathieuCharacteristicExponent](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java)

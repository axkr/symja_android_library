## MathieuSPrime

```
MathieuSPrime(a, q, z)
```

> returns the derivative with respect to `z` of the odd Mathieu function `MathieuS(a, q, z)`.

The Mathieu functions solve `y''(z) + (a - 2*q*Cos(2*z))*y(z) == 0` for an arbitrary characteristic value `a`. They are the even and the odd part of the Floquet solution `Sum(c(k)*Exp(I*(nu+2*k)*z), {k,-Infinity,Infinity})` with `Sum(Abs(c(k))^2, k) == 1`, so that `MathieuC(a, 0, z) == Cos(Sqrt(a)*z)` and `MathieuS(a, 0, z) == Sin(Sqrt(a)*z)`.

See
* [Wikipedia - Mathieu function](https://en.wikipedia.org/wiki/Mathieu_function)
* [NIST Digital Library of Mathematical Functions - Mathieu Functions](https://dlmf.nist.gov/28)

### Examples

```
>> MathieuSPrime(2, 1, 0.)
0.533616

>> MathieuSPrime(a, 0, z)
Sqrt(a)*Cos(Sqrt(a)*z)

>> D(MathieuSPrime(a, q, z), z)
(-a+2*q*Cos(2*z))*MathieuS(a,q,z)
```

### Related terms
[MathieuC](MathieuC.md), [MathieuS](MathieuS.md), [MathieuCPrime](MathieuCPrime.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of MathieuSPrime](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java)

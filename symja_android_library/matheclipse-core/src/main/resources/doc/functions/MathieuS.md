## MathieuS

```
MathieuS(a, q, z)
```

> returns the odd Mathieu function with characteristic value `a` and parameter `q`.

The Mathieu functions solve `y''(z) + (a - 2*q*Cos(2*z))*y(z) == 0` for an arbitrary characteristic value `a`. They are the even and the odd part of the Floquet solution `Sum(c(k)*Exp(I*(nu+2*k)*z), {k,-Infinity,Infinity})` with `Sum(Abs(c(k))^2, k) == 1`, so that `MathieuC(a, 0, z) == Cos(Sqrt(a)*z)` and `MathieuS(a, 0, z) == Sin(Sqrt(a)*z)`.

See
* [Wikipedia - Mathieu function](https://en.wikipedia.org/wiki/Mathieu_function)
* [NIST Digital Library of Mathematical Functions - Mathieu Functions](https://dlmf.nist.gov/28)

### Examples

```
>> MathieuS(2, 1, 3.2)
-0.768098

>> MathieuS(a, 0, z)
Sin(Sqrt(a)*z)

>> D(MathieuS(a, q, z), z)
MathieuSPrime(a,q,z)
```

### Related terms
[MathieuC](MathieuC.md), [MathieuCPrime](MathieuCPrime.md), [MathieuSPrime](MathieuSPrime.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of MathieuS](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java)

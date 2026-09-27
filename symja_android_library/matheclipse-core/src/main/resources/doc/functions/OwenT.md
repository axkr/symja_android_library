## OwenT

```
OwenT(x, a)
```

> gives Owen's T function `1/(2*Pi) * Integrate(E^(-x^2*(1+t^2)/2)/(1+t^2), {t, 0, a})`.

`OwenT(x, a)` is even in `x` and odd in `a`. It is evaluated to arbitrary precision by Owen's series T1 for `Abs(a) <= 1` and by the identity `OwenT(x, a) = (Phi(x)+Phi(a*x))/2 - Phi(x)*Phi(a*x) - OwenT(a*x, 1/a)` for `Abs(a) > 1`, where `Phi` is the standard normal distribution function.

See
* [Wikipedia - Owen's T function](https://en.wikipedia.org/wiki/Owen%27s_T_function)

### Examples

```
>> OwenT(x, 1)
1/8*Erfc(x/Sqrt(2))*Erfc(-x/Sqrt(2))

>> OwenT(0, a)
ArcTan(a)/(2*Pi)

>> OwenT(-x, a)
OwenT(x,a)

>> OwenT(x, -a)
-OwenT(x,a)

>> OwenT(4.0, 1)
0.0000158351

>> N(OwenT(1/8, -1), 50)
-0.12376305449537457063916405905206417131142512192856

>> D(OwenT(x, a), x)
-Erf((a*x)/Sqrt(2))/(2*E^(x^2/2)*Sqrt(2*Pi))

>> D(OwenT(x, a), a)
E^(1/2*(-1-a^2)*x^2)/(2*(1+a^2)*Pi)
```

### Related terms

[Erf](Erf.md), [Erfc](Erfc.md), [SkewNormalDistribution](SkewNormalDistribution.md)

### Implementation status

* &#x1F9EA; - experimental

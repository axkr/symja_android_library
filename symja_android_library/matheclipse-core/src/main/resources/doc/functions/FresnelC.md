## FresnelC

```
FresnelC(z)
```

> gives the Fresnel integral `Integrate(Cos(Pi*t^2/2), {t, 0, z})`.

`FresnelC(z)` is odd in `z`, and `FresnelC(I*z) == I*FresnelC(z)`. It tends to `1/2` for `z -> Infinity`. It evaluates numerically with an inexact number or in `N(..., n)`, also for complex `z`.

See
* [Wikipedia - Fresnel integral](https://en.wikipedia.org/wiki/Fresnel_integral)
* [NIST - Fresnel integrals, definitions](https://dlmf.nist.gov/7.2)

### Examples

```
>> FresnelC(0)
0

>> FresnelC(Infinity)
1/2

>> FresnelC(-x)
-FresnelC(x)

>> FresnelC(I*x)
I*FresnelC(x)

>> FresnelC({0.5, 1.0, 1.5})
{0.492344,0.779893,0.445261}

>> N(FresnelC(1), 30)
0.779893400376822829474206413653

>> FresnelC(1.0+I)
2.55579+I*2.55579

>> D(FresnelC(x), x)
Cos(1/2*Pi*x^2)

>> Integrate(Cos(Pi*t^2/2), {t, 0, x})
FresnelC(x)

>> Integrate(FresnelC(x), x)
x*FresnelC(x)-Sin(1/2*Pi*x^2)/Pi

>> Series(FresnelC(x), {x, 0, 5})
x-1/40*Pi^2*x^5+O(x)^6
```

### Related terms

[Erf](Erf.md), [FresnelS](FresnelS.md)

### Implementation status

* &#x2705; - full supported

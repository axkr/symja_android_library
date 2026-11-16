## FresnelS

```
FresnelS(z)
```

> gives the Fresnel integral `Integrate(Sin(Pi*t^2/2), {t, 0, z})`.

`FresnelS(z)` is odd in `z`, and `FresnelS(I*z) == -I*FresnelS(z)`. It tends to `1/2` for `z -> Infinity`. It evaluates numerically with an inexact number or in `N(..., n)`, also for complex `z`.

See
* [Wikipedia - Fresnel integral](https://en.wikipedia.org/wiki/Fresnel_integral)
* [NIST - Fresnel integrals, definitions](https://dlmf.nist.gov/7.2)

### Examples

```
>> FresnelS(0)
0

>> FresnelS(Infinity)
1/2

>> FresnelS(-x)
-FresnelS(x)

>> FresnelS(I*x)
-I*FresnelS(x)

>> FresnelS(1.0)
0.438259

>> N(FresnelS(1), 30)
0.438259147390354766076756696625

>> FresnelS(1.0+I)
-2.06189+I*2.06189

>> D(FresnelS(x), x)
Sin(1/2*Pi*x^2)

>> Integrate(Sin(Pi*t^2/2), {t, 0, x})
FresnelS(x)

>> Integrate(FresnelS(x), x)
Cos(1/2*Pi*x^2)/Pi+x*FresnelS(x)

>> Series(FresnelS(x), {x, 0, 7})
1/6*Pi*x^3-1/336*Pi^3*x^7+O(x)^8
```

### Related terms

[Erf](Erf.md), [FresnelC](FresnelC.md)

### Implementation status

* &#x2705; - full supported

## DawsonF

```
DawsonF(z)
```

> gives the Dawson integral `E^(-z^2)*Integrate(E^(t^2), {t, 0, z})`.

`DawsonF(z)` is odd in `z`. `FunctionExpand` rewrites it with the imaginary error function `Erfi`. It evaluates numerically with an inexact number or in `N(..., n)`, also for complex `z`.

See
* [Wikipedia - Dawson function](https://en.wikipedia.org/wiki/Dawson_function)
* [NIST - Dawson's integral, definitions](https://dlmf.nist.gov/7.2)

### Examples

```
>> DawsonF(0)
0

>> DawsonF(-x)
-DawsonF(x)

>> DawsonF({0.5, 1.0, 2.0})
{0.424436,0.53808,0.30134}

>> N(DawsonF(1), 30)
0.538079506912768419136387420408

>> DawsonF(1.0+I)
0.990373+I*(-0.638873)

>> FunctionExpand(DawsonF(x))
(Sqrt(Pi)*Erfi(x))/(2*E^x^2)

>> D(DawsonF(x), x)
1-2*x*DawsonF(x)

>> Series(DawsonF(x), {x, 0, 5})
x-2/3*x^3+4/15*x^5+O(x)^6
```

The maximum of `DawsonF(x)` for real `x` is at the root of its derivative `1-2*x*DawsonF(x)`:

```
>> FindRoot(1-2*x*DawsonF(x), {x, 1})
{x->0.924139}
```

### Related terms

[Erf](Erf.md), [Erfi](Erfi.md), [FunctionExpand](FunctionExpand.md)

### Implementation status

* &#x2705; - full supported

## KelvinBer

```
KelvinBer(z)
```

> returns the Kelvin function `ber(z)` of order `0`.

```
KelvinBer(n, z)
```

> returns the Kelvin function `ber(n, z)` of order `n`.

`KelvinBer` and [KelvinBei](KelvinBei.md) are the real and the imaginary part of a Bessel function on the line `E^(3/4*Pi*I)` of the complex plane: for a positive `z`,

```
KelvinBer(n, z) + I*KelvinBei(n, z) == BesselJ(n, z*E^(3/4*Pi*I))
```

They arise from the equation `z^2*w'' + z*w' - (I*z^2 + n^2)*w == 0`, for example for the current density in a round wire carrying alternating current. `KelvinBer(z)` is `KelvinBer(0, z)`.

`KelvinBer(0)` is `1`, and `KelvinBer(n, 0)` is `0` for any other integer `n`. With numbers, one of them inexact, or in `N(..., n)`, it evaluates numerically, also for a complex order and argument. For an integer order it is an even or odd function of `z`, and real for a real `z`. `FunctionExpand` writes it with `BesselJ`.

`KelvinBer` is `Listable`.

See
* [Wikipedia - Kelvin functions](https://en.wikipedia.org/wiki/Kelvin_functions)
* [NIST - Kelvin functions, definitions and basic properties](https://dlmf.nist.gov/10.61)

### Examples

```
>> KelvinBer(2.0)
0.751734

>> N(KelvinBer(2), 30)
0.751734182713808228550998901231

>> KelvinBer(1/2, 2.0)
-0.307238

>> KelvinBer(2.0+I)
1.1058+I*(-0.377274)

>> KelvinBer(-2.0)
0.751734

>> KelvinBer(0)
1
```

The relation with `BesselJ`:

```
>> Chop(KelvinBer(2.0) + I*KelvinBei(2.0) - BesselJ(0, 2.0*Exp(3*Pi*I/4)))
0

>> FunctionExpand(KelvinBer(x))
1/2*(BesselJ(0,((-1-I)*x)/Sqrt(2))+BesselJ(0,((-1+I)*x)/Sqrt(2)))
```

```
>> Series(KelvinBer(x), {x, 0, 8})
1-x^4/64+x^8/147456+O(x)^9

>> D(KelvinBer(x), x)
(2*KelvinBei(1,x)+2*KelvinBer(1,x))/(2*Sqrt(2))
```

### Related terms
[BesselJ](BesselJ.md), [KelvinBei](KelvinBei.md), [KelvinKei](KelvinKei.md), [KelvinKer](KelvinKer.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of KelvinBer](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BesselFunctions.java)

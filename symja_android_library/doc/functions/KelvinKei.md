## KelvinKei

```
KelvinKei(z)
```

> returns the Kelvin function `kei(z)` of order `0`.

```
KelvinKei(n, z)
```

> returns the Kelvin function `kei(n, z)` of order `n`.

[KelvinKer](KelvinKer.md) and `KelvinKei` are the real and the imaginary part of a modified Bessel function of the second kind on the line `E^(1/4*Pi*I)` of the complex plane: for a positive `z`,

```
KelvinKer(n, z) + I*KelvinKei(n, z) == E^(-n*Pi*I/2)*BesselK(n, z*E^(1/4*Pi*I))
```

`KelvinKei(z)` is `KelvinKei(0, z)`, and `KelvinKei(0)` is `-Pi/4`. Like `BesselK`, it has a branch cut along the negative real axis, so it is complex for a negative `z`.

With numbers, one of them inexact, or in `N(..., n)`, it evaluates numerically, also for a complex order and argument. For a non-integer numeric order, `FunctionExpand` writes it with `BesselI`; a half-integer order reduces further to elementary functions.

`KelvinKei` is `Listable`.

See
* [Wikipedia - Kelvin functions](https://en.wikipedia.org/wiki/Kelvin_functions)
* [NIST - Kelvin functions, definitions and basic properties](https://dlmf.nist.gov/10.61)

### Examples

```
>> KelvinKei(2.0)
-0.2024

>> N(KelvinKei(2), 30)
-0.20240006776470428829205133744

>> KelvinKei(1.5, 2.0)
0.288502

>> KelvinKei(-2.0)
-0.2024+I*(-3.05454)

>> KelvinKei(0)
-Pi/4
```

```
>> D(KelvinKei(n, x), x)
(-KelvinKei(-1+n,x)+KelvinKei(1+n,x)+KelvinKer(-1+n,x)-KelvinKer(1+n,x))/(2*Sqrt(
2))
```

### Related terms
[BesselK](BesselK.md), [KelvinBei](KelvinBei.md), [KelvinBer](KelvinBer.md), [KelvinKer](KelvinKer.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of KelvinKei](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BesselFunctions.java)

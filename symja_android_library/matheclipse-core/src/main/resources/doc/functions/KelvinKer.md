## KelvinKer

```
KelvinKer(z)
```

> returns the Kelvin function `ker(z)` of order `0`.

```
KelvinKer(n, z)
```

> returns the Kelvin function `ker(n, z)` of order `n`.

`KelvinKer` and [KelvinKei](KelvinKei.md) are the real and the imaginary part of a modified Bessel function of the second kind on the line `E^(1/4*Pi*I)` of the complex plane: for a positive `z`,

```
KelvinKer(n, z) + I*KelvinKei(n, z) == E^(-n*Pi*I/2)*BesselK(n, z*E^(1/4*Pi*I))
```

`KelvinKer(z)` is `KelvinKer(0, z)`. Like `BesselK`, it has a branch cut along the negative real axis, so it is complex for a negative `z`, and `KelvinKer(n, 0)` is `ComplexInfinity` for an integer `n != 0`.

With numbers, one of them inexact, or in `N(..., n)`, it evaluates numerically, also for a complex order and argument. For a non-integer numeric order, `FunctionExpand` writes it with `BesselI`; a half-integer order reduces further to elementary functions.

`KelvinKer` is `Listable`.

See
* [Wikipedia - Kelvin functions](https://en.wikipedia.org/wiki/Kelvin_functions)
* [NIST - Kelvin functions, definitions and basic properties](https://dlmf.nist.gov/10.61)

### Examples

```
>> KelvinKer(2.0)
-0.0416645

>> N(KelvinKer(2), 30)
-0.0416645139915095322586822239139

>> KelvinKer(1, 2.0)
-0.230806

>> KelvinKer(0.5, 1.0+2.0*I)
-0.59371+I*0.550767

>> KelvinKer(-2.0)
-0.0416645+I*(-2.36164)

>> KelvinKer(1, 0)
ComplexInfinity
```

The relation with `BesselK`:

```
>> Chop(KelvinKer(2.0) + I*KelvinKei(2.0) - BesselK(0, 2.0*Exp(Pi*I/4)))
0
```

```
>> D(KelvinKer(n, x), x)
(-KelvinKei(-1+n,x)+KelvinKei(1+n,x)-KelvinKer(-1+n,x)+KelvinKer(1+n,x))/(2*Sqrt(
2))
```

### Related terms
[BesselK](BesselK.md), [KelvinBei](KelvinBei.md), [KelvinBer](KelvinBer.md), [KelvinKei](KelvinKei.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of KelvinKer](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BesselFunctions.java)

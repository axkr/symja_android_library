## KelvinBei

```
KelvinBei(z)
```

> returns the Kelvin function `bei(z)` of order `0`.

```
KelvinBei(n, z)
```

> returns the Kelvin function `bei(n, z)` of order `n`.

[KelvinBer](KelvinBer.md) and `KelvinBei` are the real and the imaginary part of a Bessel function on the line `E^(3/4*Pi*I)` of the complex plane: for a positive `z`,

```
KelvinBer(n, z) + I*KelvinBei(n, z) == BesselJ(n, z*E^(3/4*Pi*I))
```

`KelvinBei(z)` is `KelvinBei(0, z)`.

`KelvinBei(n, 0)` is `0` for an integer `n`. With numbers, one of them inexact, or in `N(..., n)`, it evaluates numerically, also for a complex order and argument. For an integer order it is an even or odd function of `z`, and real for a real `z`. `FunctionExpand` writes it with `BesselJ`.

`KelvinBei` is `Listable`.

See
* [Wikipedia - Kelvin functions](https://en.wikipedia.org/wiki/Kelvin_functions)
* [NIST - Kelvin functions, definitions and basic properties](https://dlmf.nist.gov/10.61)

### Examples

```
>> KelvinBei(2.0)
0.972292

>> N(KelvinBei(2), 30)
0.972291627306661206104032220226

>> KelvinBei(1.5, 3.0)
-1.46644

>> KelvinBei({1.0, 2.0, 3.0})
{0.249566,0.972292,1.93759}
```

```
>> FunctionExpand(KelvinBei(x))
-I*1/2*(-BesselJ(0,((-1-I)*x)/Sqrt(2))+BesselJ(0,((-1+I)*x)/Sqrt(2)))

>> Series(KelvinBei(x), {x, 0, 10})
x^2/4-x^6/2304+x^10/14745600+O(x)^11
```

```
>> D(KelvinBei(n, x), x)
(-KelvinBei(-1+n,x)+KelvinBei(1+n,x)+KelvinBer(-1+n,x)-KelvinBer(1+n,x))/(2*Sqrt(
2))
```

### Related terms
[BesselJ](BesselJ.md), [KelvinBer](KelvinBer.md), [KelvinKei](KelvinKei.md), [KelvinKer](KelvinKer.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of KelvinBei](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BesselFunctions.java)

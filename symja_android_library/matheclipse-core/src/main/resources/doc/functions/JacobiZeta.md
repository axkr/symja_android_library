## JacobiZeta

```
JacobiZeta(phi, m)
```

> returns the Jacobi zeta function `Z(phi, m)` of the amplitude `phi` and the parameter `m`.

It is defined by the incomplete and complete elliptic integrals

```
JacobiZeta(phi, m) == EllipticE(phi, m) - EllipticE(m)/EllipticK(m)*EllipticF(phi, m)
```

so that `JacobiZeta(JacobiAmplitude(u, m), m)` is the Jacobi zeta function of the argument `u`.

The value is `0` for `m == 0`, at `phi == 0` and at `phi == Pi/2`. For `m == 1` it is `Sin(phi)` if `Abs(Re(phi)) <= Pi/2`. For `m -> Infinity` it is `ComplexInfinity`. Otherwise it evaluates numerically for machine numbers, and to the requested precision with `N(..., n)`; for exact arguments it stays unevaluated.

See
* [Wikipedia - Elliptic integral, Jacobi zeta function](https://en.wikipedia.org/wiki/Elliptic_integral#Jacobi_zeta_function)
* [NIST - Jacobi's epsilon and zeta functions](https://dlmf.nist.gov/22.16#iii)

### Examples

```
>> JacobiZeta(0.5, 0.3)
0.0671513

>> JacobiZeta(1.0+0.5*I, 0.3)
0.119529+I*(-0.0348916)
```

```
>> JacobiZeta(1/2, 1/3)
JacobiZeta(1/2,1/3)

>> N(JacobiZeta(1/2, 1/3), 30)
0.0752145377978527500917930522077
```

The value agrees with the definition by elliptic integrals:

```
>> JacobiZeta(1.2, 0.3) - (EllipticE(1.2, 0.3) - EllipticE(0.3)/EllipticK(0.3)*EllipticF(1.2, 0.3))
0.0
```

The Jacobi zeta function of the argument `u == 0.7`:

```
>> JacobiZeta(JacobiAmplitude(0.7, 0.3), 0.3)
0.0793154
```

The special values:

```
>> {JacobiZeta(z, 0), JacobiZeta(0, m), JacobiZeta(Pi/2, m), JacobiZeta(1/2, 1), JacobiZeta(z, Infinity)}
{0,0,0,Sin(1/2),ComplexInfinity}
```

### Related terms
[EllipticE](EllipticE.md), [EllipticF](EllipticF.md), [EllipticK](EllipticK.md), [JacobiAmplitude](JacobiAmplitude.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of JacobiZeta](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java)

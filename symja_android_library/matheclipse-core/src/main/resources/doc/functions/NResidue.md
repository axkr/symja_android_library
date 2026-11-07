## NResidue

```
NResidue(expr, {z, z0})
```

> numerically computes the residue of `expr` at the point `z = z0`.

The residue is the coefficient of `(z-z0)^(-1)` in the Laurent series of `expr` at `z0`. `NResidue` computes it as the contour integral of `expr` around a small circle centered at `z0`, divided by `2*Pi*I`. Unlike the symbolic `Residue`, it doesn't need a series expansion, so it also works for an essential singularity like the one of `Exp(1/z)` at `0`. The circle must not enclose another singularity of `expr`.

The result is a complex number; small rounding errors in the real or imaginary part can be removed with [Chop](Chop.md). A list of expressions gives the list of their residues. If the integral doesn't converge to the requested accuracy, a message is printed.

See:
* [Wikipedia - Residue (complex analysis)](https://en.wikipedia.org/wiki/Residue_(complex_analysis))

### Options

- `Radius` - the radius of the circle, `1/100` by default; `Radius -> Automatic` chooses the radius automatically
- `WorkingPrecision` - the number of digits used in the computation; the default `MachinePrecision` computes with machine numbers
- `PrecisionGoal` and `AccuracyGoal` - the relative and the absolute accuracy, in digits, which the integral should reach
- `MaxRecursion` - the maximum number of times the number of sample points on the circle is doubled

### Examples

```
>> NResidue(1/x, {x, 0}) // Chop
1.0

>> NResidue(1/(z^2+1), {z, I}) // Chop
I*(-0.5)

>> NResidue(Cot(z), {z, 0}) // Chop
1.0

>> NResidue(Gamma(z), {z, -2}) // Chop
0.5
```

The residue of a double pole without a `1/(z-1)` term, and of a function without a singularity, is `0`:

```
>> NResidue(1/(z-1)^2, {z, 1}) // Chop
0

>> NResidue(z^2, {z, 0}) // Chop
0
```

Essential singularities:

```
>> NResidue(Sin(1/(10*x)), {x, 0}) // Chop
0.1

>> NResidue(Exp(1/x), {x, 0}, Radius -> 1) // Chop
1.0

>> NResidue(Exp(1/x), {x, 0}, Radius -> Automatic) // Chop
1.0
```

A list of expressions:

```
>> NResidue({1/z, 2/z}, {z, 0}) // Chop
{1.0,2.0}
```

### Related terms
[Chop](Chop.md), [ND](ND.md), [NIntegrate](NIntegrate.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of NResidue](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NResidue.java)

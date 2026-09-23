## KnotData

```
KnotData(knot, "property")
```

> gives the value of `property` for a torus knot.

```
KnotData(knot)
```

> draws the knot as a tube.

```
KnotData(All)
```

> gives the names of the knots this table knows.

A torus knot lies on the surface of a torus, winding `p` times round its axis and `q` times through
its hole. The named knots `"Trefoil"`, `"CinquefoilKnot"` and `"SeptafoilKnot"` are torus knots, and
every other one is `{"TorusKnot", {p, q}}` with `p` and `q` coprime. The properties are
`"AlexanderBriggsNotation"`, `"CrossingNumber"`, `"ImageData"` - the tube round the curve as a list
of one `GraphicsComplex`, which can be moved or scaled into a scene of its own - and
`"SpaceCurve"`. The space curve is the textbook
parametrization on a torus of major radius 2 and tube radius 1 - the same knot as the reference
implementation's, not the same coefficients.

A knot may be named as `Entity("Knot", "name")`, so `EntityValue` reaches this function too.

See
* [Wikipedia - Torus knot](https://en.wikipedia.org/wiki/Torus_knot)

### Examples

```
>> KnotData("Trefoil", "CrossingNumber")
3

>> KnotData({"TorusKnot", {3, 5}}, "CrossingNumber")
10

>> KnotData("Trefoil", "SpaceCurve")
Function({t},{Cos(2*t)*(2+Cos(3*t)),(2+Cos(3*t))*Sin(2*t),Sin(3*t)})
```

### Related terms
[PolyhedronData](PolyhedronData.md)

### Implementation status

* &#x1F9EA; - experimental

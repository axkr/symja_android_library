## KnotData

```
KnotData(knot, "property")
```

> gives the value of `property` for a knot of the Rolfsen table or a torus knot.

```
KnotData(knot)
```

> draws the knot as a tube.

```
KnotData()
```

> gives the standard names of the famous knots.

```
KnotData(All)
```

> gives every knot of the table as `{n, k}`.

A knot is named by its entry `{n, k}` in the Rolfsen table of prime knots - the `k`-th knot with `n`
crossings, up to ten crossings, `{0, 1}` being the unknot - by a standard name (`"Unknot"`,
`"Trefoil"`, `"FigureEight"`, `"SolomonSeal"`, `"Stevedore"`, `"PerkoPair"`), or as a torus knot
`{"TorusKnot", {p, q}}` with `p` and `q` coprime, which winds `p` times round a torus's axis and `q`
times through its hole.

The properties are `"AlexanderBriggsList"` (`{n, k}`), `"AlexanderBriggsNotation"`
(`Subscript(n, k)`), `"CrossingNumber"`, `"Name"`, `"StandardName"`, `"SpaceCurve"` - a pure
function of the curve parameter - and `"ImageData"`, the tube round the curve as a list of one
`GraphicsComplex`, which can be moved or scaled into a scene of its own. A space curve is known for
the trefoil, whose classic `{Sin(t) + 2 Sin(2 t), Cos(t) - 2 Cos(2 t), -Sin(3 t)}` is the reference
implementation's too, and for the torus knots, as the textbook parametrization on a torus of major
radius 2 and tube radius 1.

A knot may be named as `Entity("Knot", "name")`, so `EntityValue` reaches this function too.

See
* [Wikipedia - Knot table](https://en.wikipedia.org/wiki/List_of_prime_knots)
* [Wikipedia - Torus knot](https://en.wikipedia.org/wiki/Torus_knot)

### Examples

```
>> KnotData("Trefoil", "CrossingNumber")
3

>> KnotData({5, 2}, "StandardName")
{Knot,{5,2}}

>> KnotData({"TorusKnot", {3, 5}}, "CrossingNumber")
10

>> KnotData("Trefoil", "SpaceCurve")
{Sin(#1)+2*Sin(2*#1),Cos(#1)-2*Cos(2*#1),-Sin(3*#1)}&

>> Length(KnotData(All))
250
```

### Related terms
[PolyhedronData](PolyhedronData.md)

### Implementation status

* &#x1F9EA; - experimental

## IsotopeData

```
IsotopeData(element)
```

> gives the isotopes of the element, as entities.

```
IsotopeData(isotope, "property")
```

> gives the value of the property for that isotope.

```
IsotopeData()
```

> gives every isotope in the table, as entities.

An element can be named, given by atomic number, or given by symbol. A single isotope is named by
an `Entity("Isotope", name)`, by the name with its mass number written onto it - `"Carbon12"` or
`"C12"` - or by the element and mass number as a pair, `{"Carbon", 12}` or `{6, 12}`. A property can
be named or given as an `EntityProperty("Isotope", property)`.

`IsotopeData` reads the isotope table bundled with the
[Chemistry Development Kit](https://cdk.github.io/), which is the
[Blue Obelisk Data Repository](https://github.com/BlueObelisk/bodr) table: every isotope of every
element, 3171 of them, not only the ones that occur in nature. It lives in `matheclipse-chem`, so
without that module on the classpath `IsotopeData` stays unevaluated as the rest of that module's
functions do.

### Examples

```
>> IsotopeData({"Carbon", 12})
Entity(Isotope,Carbon12)

>> Take(IsotopeData(6), 3)
{Entity(Isotope,Carbon8),Entity(Isotope,Carbon9),Entity(Isotope,Carbon10)}

>> IsotopeData(Entity("Isotope", "Carbon12"), "NeutronNumber")
6

>> IsotopeData(Entity("Isotope", "Carbon12"), "AtomicMass")
Quantity(12.0,"Daltons")

>> Length(IsotopeData())
3171
```

The properties one isotope answers for:

```
>> IsotopeData("Properties")
{EntityProperty(Isotope,AtomicMass),EntityProperty(Isotope,AtomicNumber),EntityProperty(Isotope,BindingEnergy),EntityProperty(Isotope,IsotopeAbundance),EntityProperty(Isotope,MassNumber),EntityProperty(Isotope,NeutronNumber),EntityProperty(Isotope,StandardName)}
```

`BindingEnergy` is not looked up. It is the mass excess of the nucleus,
`(Z m(1H) + N m(n) - M(A,Z)) c^2`, with the neutron mass and the u &rarr; MeV factor from
[CODATA 2018](https://physics.nist.gov/cuu/Constants/) and the isotope masses from the table. The
electron binding energies this glosses over are of order electronvolts, which does not show at this
scale, so the result reproduces the textbook values - carbon-12's 92.16 MeV, and the 8.79 MeV per
nucleon at the iron-56 peak of the curve:

```
>> Round(QuantityMagnitude(IsotopeData(Entity("Isotope", "Carbon12"), "BindingEnergy")), 0.01)
92.16

>> ListLinePlot(Table({m, IsotopeData(Entity("Isotope", "Iron" <> ToString(m)), "BindingEnergy")/m}, {m, 54, 58}))

```

### Abundances are reported in two different units

The table records natural abundance as a percentage, and the element forms hand that on unchanged.
An isotope entity answers with a fraction of one, which is what the reference implementation gives
for the same question:

```
>> IsotopeData("C", "Abundances")
{12->98.93,13->1.07}

>> IsotopeData(Entity("Isotope", "Carbon12"), "IsotopeAbundance")
0.9893
```

An isotope that does not occur in nature has no abundance to report:

```
>> IsotopeData(Entity("Isotope", "Carbon14"), "IsotopeAbundance")
Missing(NotAvailable)
```

### The whole element

`"MassNumbers"`, `"Abundances"` and `"StableIsotopes"` are questions about the element rather than
about one isotope; every other property of an element speaks for its most abundant isotope.
[ElementData](ElementData.md) reads the first two for its own `"KnownIsotopes"` and
`"IsotopeAbundances"`, and the third for `"StableIsotopes"`.

```
>> IsotopeData("C", "MassNumbers")
{8,9,10,11,12,13,14,15,16,17,18,19,20,21,22}

>> ElementData(6, "StableIsotopes")
{Entity(Isotope,Carbon12),Entity(Isotope,Carbon13)}
```

"Stable" there means the table records a natural abundance for it, which is a coarser cut than
nuclear stability: it takes in the long lived primordial isotopes that still occur - potassium-40,
thorium-232, all three natural uranium isotopes - and leaves technetium and polonium with none at
all, which is how a chemistry reference describes them.

```
>> ElementData(92, "StableIsotopes")
{Entity(Isotope,Uranium234),Entity(Isotope,Uranium235),Entity(Isotope,Uranium238)}

>> ElementData(84, "StableIsotopes")
{}
```

### Isotopes as entities

The type is registered, so the generic entity functions reach it:

```
>> EntityValue(Entity("Isotope", "Carbon12"), {"MassNumber", "AtomicNumber"})
{12,6}

>> Length(EntityList("Isotope"))
3171
```

An isotope the table does not have is no answer at all rather than missing data, which is what lets
`EntityValue` say which half of the question it did not know:

```
>> EntityValue(Entity("Isotope", "Carbon99"), "MassNumber")
Missing(UnknownEntity,{Isotope,Carbon99})

>> EntityValue(Entity("Isotope", "Carbon12"), "Nonsense")
Missing(UnknownProperty,{Isotope,Nonsense})
```

### Related terms

[ElementData](ElementData.md), [Entity](Entity.md), [EntityList](EntityList.md),
[EntityValue](EntityValue.md), [Quantity](Quantity.md)

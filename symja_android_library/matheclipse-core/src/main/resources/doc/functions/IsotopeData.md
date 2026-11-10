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
an `Entity("Isotope", name)`, or by the name with its mass number written onto it - `"Carbon12"` or
`"C12"`. A property can be named or given as an `EntityProperty("Isotope", property)`.

`IsotopeData` reads the NUBASE2020 nuclide chart (F.G. Kondev et al., Chinese Physics C 45 (2021)
030001, from the [IAEA Atomic Mass Data Center](https://www-nds.iaea.org/amdc/)): every known ground
state of every element, 3558 of them, not only the ones that occur in nature, with half-lives and
decay modes. The free neutron is in the chart too, but it is no isotope of an element and so is not
an entity here. Masses and natural abundances come from the isotope table bundled with the
[Chemistry Development Kit](https://cdk.github.io/), the
[Blue Obelisk Data Repository](https://github.com/BlueObelisk/bodr) table, wherever it has the
nuclide. It lives in `matheclipse-chem`, so without that module on the classpath `IsotopeData`
stays unevaluated as the rest of that module's functions do.

### Examples

```
>> IsotopeData("Carbon12")
Entity(Isotope,Carbon12)

>> Take(IsotopeData(6), 3)
{Entity(Isotope,Carbon8),Entity(Isotope,Carbon9),Entity(Isotope,Carbon10)}

>> IsotopeData(Entity("Isotope", "Carbon12"), "NeutronNumber")
6

>> IsotopeData(Entity("Isotope", "Carbon12"), "AtomicMass")
Quantity(12.0,"AtomicMassUnit")

>> Length(IsotopeData())
3558
```

The properties one isotope answers for:

```
>> IsotopeData("Properties")
{EntityProperty(Isotope,AtomicMass),EntityProperty(Isotope,AtomicNumber),EntityProperty(Isotope,BindingEnergy),EntityProperty(Isotope,BranchingRatios),EntityProperty(Isotope,DaughterNuclides),EntityProperty(Isotope,DecayModes),EntityProperty(Isotope,HalfLife),EntityProperty(Isotope,IsotopeAbundance),EntityProperty(Isotope,Lifetime),EntityProperty(Isotope,MassNumber),EntityProperty(Isotope,NeutronNumber),EntityProperty(Isotope,StandardName),EntityProperty(Isotope,Symbol)}
```

`BindingEnergy` is the binding energy **per nucleon**, which is the quantity the curve of binding
energy is drawn from. It is not looked up: it is the mass excess of the nucleus,
`(Z m(1H) + N m(n) - M(A,Z)) c^2 / A`, with the neutron mass and the u &rarr; MeV factor from
[CODATA 2018](https://physics.nist.gov/cuu/Constants/) and the isotope masses from the table. The
electron binding energies this glosses over are of order electronvolts, which does not show at this
scale, so the result matches the reference implementation to the figures it prints - carbon-12's
7.68 MeV, and the 8.79 MeV at the iron-56 peak:

```
>> Round(QuantityMagnitude(IsotopeData(Entity("Isotope", "Carbon12"), "BindingEnergy")), 0.0001)
7.6801

>> ListLinePlot(Table({m, IsotopeData(Entity("Isotope", "Iron" <> ToString(m)), "BindingEnergy")/m}, {m, 54, 58}))

```

### Decay

`"HalfLife"` is a quantity in the unit the chart states it in, known to the precision the chart's
uncertainty leaves - carbon-14's 5.70 kiloyears are known to 0.03, which is a precision of
`Log10(5.70/0.03)`. A half-life without an uncertainty is a machine number, a stable nuclide's is
infinite, and a particle-unstable one's is `Missing("Unstable")`. Yoctoseconds become
zeptoseconds, and a half-life of years is given in years below 10^5 years, in megayears below
10^8, in gigayears below 10^13 and in years above that. `"Lifetime"` is the half-life divided by
`Log(2)`.

```
>> IsotopeData("Carbon14", "HalfLife")
Quantity(5.7*10^3,"Years")

>> IsotopeData("Uranium238", "HalfLife")
Quantity(4.46,"Gigayears")
```

`"DecayModes"`, `"DaughterNuclides"` and `"BranchingRatios"` list the ways a nuclide decays, most
frequent first. A fission has no single daughter; a stable nuclide has no decay at all; an intensity
the chart does not give is `Missing(Unknown)`:

```
>> IsotopeData("Uranium234", "DecayModes")
{alpha emission,spontaneous fission,cluster emission,double cluster emission}

>> IsotopeData("Uranium234", "DaughterNuclides")
{Entity(Isotope,Thorium230),Missing(Variable),Entity(Isotope,Mercury206),Entity(Isotope,Hafnium184)}

>> IsotopeData("Oxygen16", "DecayModes")
Missing(NotApplicable)
```

The chart counts an emission that follows a beta decay inside that beta decay; here every branch is
one way the nucleus ends up, so the plain beta decay keeps what the delayed emissions leave of it.
Lithium-9 emits a delayed neutron in 50.5% of its decays, and so is left with 49.5% of plain
electron emission:

```
>> IsotopeData("Lithium9", "DecayModes")
{electron + delayed neutron,electron emission}
```

`"Symbol"` writes the mass number before the element symbol, `Row({Superscript("", 14), "C"})`.

### Abundances

Natural abundance is a percentage. An isotope that does not occur in nature answers zero rather
than missing data:

```
>> IsotopeData(Entity("Isotope", "Carbon12"), "IsotopeAbundance")
Quantity(98.93,"Percent")

>> IsotopeData(Entity("Isotope", "Carbon14"), "IsotopeAbundance")
Quantity(0,"Percent")
```

### The whole element

`"MassNumbers"`, `"Abundances"` and `"StableIsotopes"` are questions about the element rather than
about one isotope; every other property of an element speaks for its most abundant isotope.
[ElementData](ElementData.md) reads the first two for its own `"KnownIsotopes"` and
`"IsotopeAbundances"`, and the third for `"StableIsotopes"`.

```
>> IsotopeData("C", "MassNumbers")
{8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23}

>> IsotopeData("C", "Abundances")
<|Entity(Isotope,Carbon12)->Quantity(98.93,"Percent"),Entity(Isotope,Carbon13)->Quantity(1.07,"Percent")|>

>> ElementData(6, "StableIsotopes")
{Entity(Isotope,Carbon12),Entity(Isotope,Carbon13)}
```

"Stable" means stable, which is not the same thing as occurring in nature: uranium occurs in nature
and has no stable isotope at all. The table is the reference implementation's, so it keeps that
implementation's choices where they differ from the textbook's - thorium-232 counts as stable while
bismuth-209 does not - and it lists them in the order of their names, which puts ruthenium-100 ahead
of ruthenium-96.

```
>> ElementData(92, "StableIsotopes")
{}

>> ElementData(90, "StableIsotopes")
{Entity(Isotope,Thorium232)}
```

### Isotopes as entities

The type is registered, so the generic entity functions reach it:

```
>> EntityValue(Entity("Isotope", "Carbon12"), {"MassNumber", "AtomicNumber"})
{12,6}

>> Length(EntityList("Isotope"))
3558
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

[ChemicalData](ChemicalData.md), [ElementData](ElementData.md), [Entity](Entity.md), [EntityList](EntityList.md),
[EntityValue](EntityValue.md), [Quantity](Quantity.md)

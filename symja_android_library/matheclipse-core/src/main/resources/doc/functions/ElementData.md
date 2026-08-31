## ElementData

```
ElementData()
```

> gives the list of all chemical elements, as entities.

```
ElementData("name", "property")
```
> gives the value of the property for the chemical specified by name.

```
ElementData(n, "property")
```

> gives the value of the property for the nth chemical element.

An element can be named, given by atomic number, given by symbol, or given as an
`Entity("Element", name)`; a property can be named or given as an
`EntityProperty("Element", property)`.

`ElementData` uses data from [Wikipedia - List of data references for chemical elements](https://en.wikipedia.org/wiki/List_of_data_references_for_chemical_elements)

A few properties are worked out rather than looked up. `ProtonCount` and `ElectronCount` follow
from the atomic number, `MolarMass` from the atomic mass, and `ValenceElectronCount` from the
electron configuration - the electrons of the outermost shell, plus those of an unfilled d subshell
one shell in and an unfilled f subshell two shells in, which gives carbon four, iron eight and
tungsten six.

`Phase` is the state an element is in at standard conditions, 25 &deg;C at one atmosphere, read off
the two temperatures at which it changes state: an element that boils at or below that temperature
is a gas there, one that melts at or below it a liquid, and everything else a solid. That gives the
eleven gases and the two liquids of the periodic table. Both temperatures have to be known to say
which of the three it is, and the fifteen heaviest elements have neither.

`NeutronCount`, `KnownIsotopes` and `IsotopeAbundances` come from the isotope table that
[IsotopeData](IsotopeData.md) reads, which lives in `matheclipse-chem`. In a build without that
module they report themselves unavailable, as everything else of that module's does.

A property that is a measurement is returned as a [Quantity](Quantity.md), so it carries its unit
and converts like any other quantity. Properties that are not measurements - an atomic number, a
Pauling electronegativity, a Mohs hardness, a Poisson ratio, a name or an electron configuration -
are returned as plain values.

### Examples

```
>> ElementData(74)
Entity(Element,Tungsten)

>> ElementData("He", "BoilingPoint")
Quantity(-268.93,"DegreesCelsius")

>> ElementData("Tungsten", "Density")
Quantity(19.25,"Centimeters"^(-3)*"Grams")

>> UnitConvert(ElementData("Tungsten", "MeltingPoint"), "Kelvins")
Quantity(3680.15,"Kelvins")

>> ElementData("Carbon", "IonizationEnergies")
{Quantity(11.26078,"MolarElectronvolts"),Quantity(24.38298,"MolarElectronvolts"),Quantity(47.88811,"MolarElectronvolts"),Quantity(64.49374,"MolarElectronvolts"),Quantity(392.0907,"MolarElectronvolts"),Quantity(489.9916,"MolarElectronvolts")}

>> ElementData(16, "ShortElectronicConfiguration")
"[Ne] 3s2 3p4"

>> ElementData(73, "ElectronicConfiguration")
{{2},{2,6},{2,6,10},{2,6,10,14},{2,6,3},{2}}

>> ElementData("Br", "Phase")
"Liquid"

>> Count(Table(ElementData(z, "Phase"), {z, 118}), "Gas")
11

>> ListPlot(Table(ElementData(z, "AtomicRadius"), {z, 118}))

```

Some properties are not appropriate for certain elements:

```
>> ElementData("He", "Electronegativity")
Missing(NotApplicable)
```

Some data is missing:

```
>> ElementData("Tc", "SpecificHeat")
Missing(NotAvailable)
```

All the known properties, as entity properties:

```
>> Length(ElementData("Properties"))
42

>> Take(ElementData("Properties"), 3)
{EntityProperty(Element,AtomicMass),EntityProperty(Element,AtomicNumber),EntityProperty(Element,AtomicRadius)}
```

The property names follow the reference implementation. A name that was used before but is not one
of them reports what replaced it and stays unevaluated, rather than answering with missing data:

```
>> ElementData("Tungsten", "Density")
ElementData(Tungsten,Density)
```

`AbsoluteMeltingPoint` and `AbsoluteBoilingPoint` are gone with them: a temperature now carries its
unit, so `UnitConvert(ElementData("Tungsten", "MeltingPoint"), "Kelvins")` is the way to Kelvin.


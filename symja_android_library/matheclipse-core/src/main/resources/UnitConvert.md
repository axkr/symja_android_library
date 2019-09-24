## UnitConvert

```
UnitConvert(quantity)
```

> convert the `quantity` to the base unit

```
UnitConvert(quantity, unit)
```

> convert the `quantity` to the given `unit`.

See 
* [Wikipedia - International System of Units](https://en.wikipedia.org/wiki/International_System_of_Units)

### Examples 

Convert radian to degree.

```
>> UnitConvert(Quantity(Pi, "rad"), "deg") 
180[deg]

>> UnitConvert(Quantity(Pi, "deg"), "rad") 
Pi^2/180[rad]
```
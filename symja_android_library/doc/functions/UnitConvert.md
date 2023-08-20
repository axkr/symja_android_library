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






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of UnitConvert](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/QuantityFunctions.java#L420) 

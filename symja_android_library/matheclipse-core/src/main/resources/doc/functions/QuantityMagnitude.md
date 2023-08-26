## QuantityMagnitude

```
QuantityMagnitude(quantity)
```

> returns the value of the `quantity` 


```
QuantityMagnitude(quantity, unit)
```

> returns the value of the `quantity` for the given `unit`

See 
* [Wikipedia - International System of Units](https://en.wikipedia.org/wiki/International_System_of_Units)

### Examples

Convert from degrees to radians

```
>> QuantityMagnitude(Quantity(360, "deg"), "rad")
2*Pi
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of QuantityMagnitude](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/QuantityFunctions.java#L341) 

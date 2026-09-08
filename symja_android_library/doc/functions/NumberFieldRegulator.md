## NumberFieldRegulator

```
NumberFieldRegulator(theta)
```

> gives the regulator of the field `Q(theta)`.

For a real quadratic field the regulator is the logarithm of the fundamental unit. For an
imaginary quadratic field the unit group is finite and the regulator is `1`.

Note that Symja may rewrite the logarithm, for example `Log(1/2*(1+Sqrt(5)))` is returned as
`ArcCsch(2)`.

See
* [Wikipedia - Dirichlet's unit theorem](https://en.wikipedia.org/wiki/Dirichlet%27s_unit_theorem)

### Examples

```
>> NumberFieldRegulator(Sqrt(2))
Log(1+Sqrt(2))

>> NumberFieldRegulator(Sqrt(3))
Log(2+Sqrt(3))

>> NumberFieldRegulator(Sqrt(-5))
1
```

### Related terms

[NumberFieldFundamentalUnits](NumberFieldFundamentalUnits.md), [NumberFieldClassNumber](NumberFieldClassNumber.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NumberFieldRegulator](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

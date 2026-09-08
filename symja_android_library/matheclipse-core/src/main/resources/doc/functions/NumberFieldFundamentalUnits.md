## NumberFieldFundamentalUnits

```
NumberFieldFundamentalUnits(theta)
```

> gives the fundamental units of the field `Q(theta)`.

For a real quadratic field there is one fundamental unit, the smallest unit greater than
`1`. It is found from the continued fraction expansion of `(1+Sqrt(D))/2` respectively `Sqrt(d)`,
which gives the least positive solution of `x^2 - D*y^2 == 4` or `x^2 - D*y^2 == -4`.

An imaginary quadratic field has only roots of unity as units, so the result is the empty list.

See
* [Wikipedia - Fundamental unit (number theory)](https://en.wikipedia.org/wiki/Fundamental_unit_(number_theory))
* [Wikipedia - Pell's equation](https://en.wikipedia.org/wiki/Pell%27s_equation)

### Examples

```
>> NumberFieldFundamentalUnits(Sqrt(2))
{1+Sqrt(2)}

>> NumberFieldFundamentalUnits(Sqrt(3))
{2+Sqrt(3)}

>> NumberFieldFundamentalUnits(Sqrt(5))
{1/2*(1+Sqrt(5))}

>> NumberFieldFundamentalUnits(Sqrt(-5))
{}
```

### Related terms

[AlgebraicUnitQ](AlgebraicUnitQ.md), [NumberFieldRegulator](NumberFieldRegulator.md), [NumberFieldRootsOfUnity](NumberFieldRootsOfUnity.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NumberFieldFundamentalUnits](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

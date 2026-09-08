## NumberFieldRootsOfUnity

```
NumberFieldRootsOfUnity(theta)
```

> gives the roots of unity in the field `Q(theta)`.

A real field contains only `1` and `-1`. Among the imaginary quadratic fields only
`Q(I)` contains the fourth roots of unity and only `Q(Sqrt(-3))` contains the sixth roots of unity;
every other one contains just `1` and `-1`. The roots are listed as ascending powers of a primitive
root of unity.

See
* [Wikipedia - Root of unity](https://en.wikipedia.org/wiki/Root_of_unity)

### Examples

```
>> NumberFieldRootsOfUnity(I)
{1,I,-1,-I}

>> NumberFieldRootsOfUnity(Sqrt(2))
{1,-1}
```

### Related terms

[NumberFieldFundamentalUnits](NumberFieldFundamentalUnits.md), [Cyclotomic](Cyclotomic.md), [AlgebraicUnitQ](AlgebraicUnitQ.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NumberFieldRootsOfUnity](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

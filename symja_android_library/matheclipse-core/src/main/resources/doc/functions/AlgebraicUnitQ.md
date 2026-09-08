## AlgebraicUnitQ

```
AlgebraicUnitQ(a)
```

> returns `True`, if `a` is an algebraic unit, and `False` otherwise.

An algebraic unit is an algebraic integer whose norm is `1` or `-1`, equivalently an
algebraic integer whose inverse is again an algebraic integer.

See
* [Wikipedia - Unit (ring theory)](https://en.wikipedia.org/wiki/Unit_(ring_theory))

### Examples

```
>> AlgebraicUnitQ(1+Sqrt(2))
True

>> AlgebraicUnitQ((1+Sqrt(5))/2)
True

>> AlgebraicUnitQ(Sqrt(2))
False

>> AlgebraicUnitQ(2)
False
```

### Related terms

[AlgebraicIntegerQ](AlgebraicIntegerQ.md), [AlgebraicNumberNorm](AlgebraicNumberNorm.md), [AlgebraicNumberQ](AlgebraicNumberQ.md), [NumberFieldFundamentalUnits](NumberFieldFundamentalUnits.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AlgebraicUnitQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

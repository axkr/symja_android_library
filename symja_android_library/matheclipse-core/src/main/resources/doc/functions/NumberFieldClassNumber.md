## NumberFieldClassNumber

```
NumberFieldClassNumber(theta)
```

> gives the class number of the field `Q(theta)`.

The class number is the order of the ideal class group, i.e. it measures how far the ring
of integers is from having unique factorization. It is `1` exactly when the ring of integers is a
unique factorization domain.

It is computed by counting the classes of primitive reduced binary quadratic forms of the field
discriminant. For a real field the cycles of indefinite forms give the narrow class number, which is
halved when the fundamental unit has norm `1`.

See
* [Wikipedia - Ideal class group](https://en.wikipedia.org/wiki/Ideal_class_group)
* [Wikipedia - Class number problem](https://en.wikipedia.org/wiki/Class_number_problem)

### Examples

```
>> NumberFieldClassNumber(Sqrt(-5))
2

>> NumberFieldClassNumber(Sqrt(-163))
1

>> NumberFieldClassNumber(Sqrt(10))
2
```

### Related terms

[NumberFieldDiscriminant](NumberFieldDiscriminant.md), [NumberFieldRegulator](NumberFieldRegulator.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NumberFieldClassNumber](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

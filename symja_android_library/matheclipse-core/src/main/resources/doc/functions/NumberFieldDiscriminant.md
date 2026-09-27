## NumberFieldDiscriminant

```
NumberFieldDiscriminant(theta)
```

> gives the discriminant of the field `Q(theta)`.

For a quadratic field `Q(Sqrt(d))` with `d` squarefree the discriminant is `d` when
`d` is congruent to `1` modulo `4`, and `4*d` otherwise.

For a degree above two it is the discriminant of the minimal polynomial of the algebraic integer
`n*theta`, divided by the square of the index of `Z[n*theta]` in the ring of integers, which is
computed with the Round 2 algorithm (see `NumberFieldIntegralBasis`).

See
* [Wikipedia - Discriminant of an algebraic number field](https://en.wikipedia.org/wiki/Discriminant_of_an_algebraic_number_field)

### Examples

```
>> NumberFieldDiscriminant(Sqrt(2))
8

>> NumberFieldDiscriminant(Sqrt(5))
5

>> NumberFieldDiscriminant(I)
-4
```

```
>> NumberFieldDiscriminant(2^(1/3))
-108

>> NumberFieldDiscriminant(Root(#^3-#^2-2*#-8&,1))
-503
```

### Related terms

[NumberFieldIntegralBasis](NumberFieldIntegralBasis.md), [NumberFieldSignature](NumberFieldSignature.md), [Discriminant](Discriminant.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NumberFieldDiscriminant](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

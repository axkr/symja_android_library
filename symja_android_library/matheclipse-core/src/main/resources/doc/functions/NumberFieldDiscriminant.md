## NumberFieldDiscriminant

```
NumberFieldDiscriminant(theta)
```

> gives the discriminant of the field `Q(theta)`.

For a quadratic field `Q(Sqrt(d))` with `d` squarefree the discriminant is `d` when
`d` is congruent to `1` modulo `4`, and `4*d` otherwise.

For a degree above two the result is only given when the discriminant of the minimal polynomial is
squarefree, because `Z[theta]` is the maximal order in that case. Otherwise the expression stays
unevaluated.

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

### Related terms

[NumberFieldIntegralBasis](NumberFieldIntegralBasis.md), [NumberFieldSignature](NumberFieldSignature.md), [Discriminant](Discriminant.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NumberFieldDiscriminant](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

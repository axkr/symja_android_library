## NumberFieldIntegralBasis

```
NumberFieldIntegralBasis(theta)
```

> gives an integral basis of the field `Q(theta)`.

The integral basis is a basis of the ring of integers of the field as a module over the
integers. For a quadratic field `Q(Sqrt(d))` it is `{1, Sqrt(d)}`, or `{1, (1+Sqrt(d))/2}` when `d`
is congruent to `1` modulo `4`.

For a degree above two the result is only given when `Z[theta]` is known to be the maximal order.

See
* [Wikipedia - Ring of integers](https://en.wikipedia.org/wiki/Ring_of_integers)

### Examples

```
>> NumberFieldIntegralBasis(Sqrt(2))
{1,Sqrt(2)}

>> NumberFieldIntegralBasis(Sqrt(5))
{1,1/2*(1+Sqrt(5))}

>> NumberFieldIntegralBasis(I)
{1,I}
```

### Related terms

[NumberFieldDiscriminant](NumberFieldDiscriminant.md), [AlgebraicIntegerQ](AlgebraicIntegerQ.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NumberFieldIntegralBasis](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

## NumberFieldSignature

```
NumberFieldSignature(theta)
```

> gives the signature `{r1, r2}` of the field `Q(theta)`.

`r1` is the number of real embeddings and `r2` the number of pairs of complex conjugate
embeddings, so `r1 + 2*r2` is the degree of the field. The real embeddings are counted exactly with
a Sturm sequence of the minimal polynomial.

See
* [Wikipedia - Algebraic number field](https://en.wikipedia.org/wiki/Algebraic_number_field)
* [Wikipedia - Sturm's theorem](https://en.wikipedia.org/wiki/Sturm%27s_theorem)

### Examples

```
>> NumberFieldSignature(Sqrt(2))
{2,0}

>> NumberFieldSignature(Sqrt(-2))
{0,1}

>> NumberFieldSignature(2^(1/3))
{1,1}
```

### Related terms

[NumberFieldDiscriminant](NumberFieldDiscriminant.md), [NumberFieldRegulator](NumberFieldRegulator.md), [MinimalPolynomial](MinimalPolynomial.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NumberFieldSignature](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

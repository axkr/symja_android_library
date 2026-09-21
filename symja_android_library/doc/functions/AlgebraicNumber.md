## AlgebraicNumber

```
AlgebraicNumber(theta, {c0, c1, ..., cn})
```

> represents the algebraic number `c0 + c1*theta + ... + cn*theta^n` of the number field `Q(theta)`.

The object is kept in a canonical form: the generator `theta` is made an algebraic integer, the
coefficient list gets one entry for each power below the degree of `theta`, and an object whose
value is rational is replaced by that rational number. A generator of degree two keeps the form it
was given in; one of a higher degree is written as the `Root` object of its minimal polynomial.
Objects of the same field combine under `+`, `*`, `/` and integer powers, computed exactly in the
field. `N`, `NumericQ`, `Abs`, `==`, `RootReduce` and `MinimalPolynomial` accept them.

See
* [Wikipedia - Algebraic number field](https://en.wikipedia.org/wiki/Algebraic_number_field)

### Examples

```
>> AlgebraicNumber(Sqrt(2),{1,1/2})+AlgebraicNumber(Sqrt(2),{1,2})
AlgebraicNumber(Sqrt(2),{2,5/2})

>> 1/AlgebraicNumber(Sqrt(2),{1,1/2})
AlgebraicNumber(Sqrt(2),{2,-1})

>> AlgebraicNumber((1+I)/2,{1,3})
AlgebraicNumber(1+I,{1,3/2})

>> AlgebraicNumber(3^(1/5),{1,2,1,3,3,1})
AlgebraicNumber(Root(-3+#1^5&,1,0),{4,2,1,3,3})

>> AlgebraicNumber(3,{1,2})
7

>> RootReduce(AlgebraicNumber(Root(#^3+#+1&,3),{1,2,1}))
Root(-1+10*#1-#1^2+#1^3&,3,0)
```

### Related terms

[AlgebraicNumberPolynomial](AlgebraicNumberPolynomial.md), [MinimalPolynomial](MinimalPolynomial.md), [Root](Root.md), [RootReduce](RootReduce.md), [ToNumberField](ToNumberField.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AlgebraicNumber](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

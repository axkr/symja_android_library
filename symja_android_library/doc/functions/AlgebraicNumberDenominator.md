## AlgebraicNumberDenominator

```
AlgebraicNumberDenominator(a)
```

> gives the smallest positive integer `n` such that `n*a` is an algebraic integer.

It is computed prime by prime from the minimal polynomial of `a`; the leading coefficient of the
primitive integer minimal polynomial can be larger (`25*x^2-10*x-49` for `1/5+Sqrt(2)`, whose
denominator is 5). It threads over lists.

### Examples

```
>> AlgebraicNumberDenominator(1/Sqrt(3))
3

>> AlgebraicNumberDenominator((1+Sqrt(5))/2)
1

>> AlgebraicNumberDenominator((1+3*I)^(-1/3))
10

>> AlgebraicNumberDenominator(1/5+Sqrt(2))
5
```

### Related terms

[AlgebraicIntegerQ](AlgebraicIntegerQ.md), [AlgebraicNumber](AlgebraicNumber.md), [MinimalPolynomial](MinimalPolynomial.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AlgebraicNumberDenominator](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

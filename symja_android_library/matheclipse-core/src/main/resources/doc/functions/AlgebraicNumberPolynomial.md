## AlgebraicNumberPolynomial

```
AlgebraicNumberPolynomial(a, x)
```

> gives the polynomial `c0 + c1*x + ...` of the algebraic number `a = AlgebraicNumber(theta, {c0, c1, ...})`, from which `a` is recovered by replacing `x` with `theta`.

A rational number is its own polynomial. It threads over lists.

### Examples

```
>> AlgebraicNumberPolynomial(AlgebraicNumber(Sqrt(2),{1,2}),x)
1+2*x

>> AlgebraicNumberPolynomial({2,AlgebraicNumber(Sqrt(2),{1,2})},x)
{2,1+2*x}

>> AlgebraicNumberPolynomial(1/2,x)
1/2
```

### Related terms

[AlgebraicNumber](AlgebraicNumber.md), [ToNumberField](ToNumberField.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AlgebraicNumberPolynomial](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

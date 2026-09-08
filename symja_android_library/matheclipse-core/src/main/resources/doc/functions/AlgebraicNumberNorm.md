## AlgebraicNumberNorm

```
AlgebraicNumberNorm(a)
```

> gives the norm of the algebraic number `a` in the field `Q(a)` it generates.

The norm is `(-1)^n` times the constant coefficient of the monic minimal polynomial of `a`,
where `n` is its degree. It is defined for an algebraic number of any degree.

See
* [Wikipedia - Field norm](https://en.wikipedia.org/wiki/Field_norm)

### Examples

```
>> AlgebraicNumberNorm(Sqrt(2))
-2

>> AlgebraicNumberNorm(1+Sqrt(2))
-1

>> AlgebraicNumberNorm(2^(1/3))
2
```

### Related terms

[AlgebraicNumberTrace](AlgebraicNumberTrace.md), [AlgebraicUnitQ](AlgebraicUnitQ.md), [MinimalPolynomial](MinimalPolynomial.md), [NumberFieldSignature](NumberFieldSignature.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AlgebraicNumberNorm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

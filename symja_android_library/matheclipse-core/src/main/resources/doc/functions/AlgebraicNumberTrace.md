## AlgebraicNumberTrace

```
AlgebraicNumberTrace(a)
```

> gives the trace of the algebraic number `a` in the field `Q(a)` it generates.

The trace is the negated second highest coefficient of the monic minimal polynomial of `a`.
It is defined for an algebraic number of any degree.

See
* [Wikipedia - Field trace](https://en.wikipedia.org/wiki/Field_trace)

### Examples

```
>> AlgebraicNumberTrace(1+Sqrt(2))
2

>> AlgebraicNumberTrace(Sqrt(2))
0

>> AlgebraicNumberTrace((1+Sqrt(5))/2)
1
```

### Related terms

[AlgebraicNumberNorm](AlgebraicNumberNorm.md), [AlgebraicUnitQ](AlgebraicUnitQ.md), [MinimalPolynomial](MinimalPolynomial.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AlgebraicNumberTrace](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberFieldFunctions.java)

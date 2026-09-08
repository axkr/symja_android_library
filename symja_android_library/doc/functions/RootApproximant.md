## RootApproximant

```
RootApproximant(x)
```

> converts the number `x` to one of the "nearby" algebraic numbers.

```
RootApproximant(x, n)
```

> converts the number `x` to an algebraic number of degree at most `n`.

The candidate polynomials are found with lattice basis reduction. For a degree `d` the rows
`(e(i) | round(K * Re(x^i)) | round(K * Im(x^i)))` span a lattice whose short vectors are the
coefficient vectors of the polynomials which nearly vanish at `x`. The scaling factor `K` is
derived from the precision of `x`.

A candidate is only accepted if its coefficients are much smaller than the ones a meaningless short
vector would have. If nothing is accepted, `x` is returned unchanged. Increasing the precision of
the input therefore increases the chance of finding the algebraic number, and reduces the chance of
a wrong answer.

See
* [Wikipedia - Algebraic number](https://en.wikipedia.org/wiki/Algebraic_number)
* [Wikipedia - Integer relation algorithm](https://en.wikipedia.org/wiki/Integer_relation_algorithm)

### Examples

```
>> RootApproximant(0.1)
1/10

>> RootApproximant(N(Sqrt(2)))
Sqrt(2)

>> RootApproximant(N(GoldenRatio))
1/2+Sqrt(5)/2
```

Numbers of higher degree are returned as `Root` objects:

```
>> RootApproximant(1.3247179572447)
Root(-1-#1+#1^3&,1,0)

>> RootApproximant(N(2^(1/5),30))
Root(-2+#1^5&,1,0)
```

A transcendental number is returned unchanged:

```
>> RootApproximant(N(Pi,30))
3.14159265358979323846264338327
```

`RootApproximant` is `Listable`:

```
>> RootApproximant({0.5, N(Sqrt(3))})
{1/2,Sqrt(3)}
```

### Related terms

[AlgebraicNumberQ](AlgebraicNumberQ.md), [LatticeReduce](LatticeReduce.md), [MinimalPolynomial](MinimalPolynomial.md), [Rationalize](Rationalize.md), [Root](Root.md), [RootReduce](RootReduce.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RootApproximant](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/RootApproximant.java#L39)

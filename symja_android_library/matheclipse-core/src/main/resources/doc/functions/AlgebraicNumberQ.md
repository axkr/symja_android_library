## AlgebraicNumberQ

```
AlgebraicNumberQ(x)
```

> returns `True`, if `x` is an explicit algebraic number, and `False` otherwise.

An expression is an explicit algebraic number if it is built up from rational numbers, complex
numbers with rational parts, `Root(...)` and `AlgebraicNumber(...)` objects with rational
coefficients, combined with `Plus`, `Times`, `Power` with a rational exponent, `Sqrt`, `CubeRoot`
and `Surd`.

The test is purely structural. Symbols are never explicit algebraic numbers, not even a symbolic
constant like `GoldenRatio` which becomes one only after `FunctionExpand`.

See
* [Wikipedia - Algebraic number](https://en.wikipedia.org/wiki/Algebraic_number)

### Examples

```
>> AlgebraicNumberQ(2^(1/3) + Sqrt(5))
True

>> AlgebraicNumberQ(Root(-2 + #1^3&, 1))
True

>> AlgebraicNumberQ(Pi)
False

>> AlgebraicNumberQ(1.5)
False
```

`AlgebraicNumberQ` is `Listable`:

```
>> AlgebraicNumberQ({1, Sqrt(2), x})
{True,True,False}
```

The same structural test is used by `Element`:

```
>> Element(Sqrt(2) + 2^(1/3), Algebraics)
True
```

### Related terms

[AlgebraicIntegerQ](AlgebraicIntegerQ.md), [Element](Element.md), [MinimalPolynomial](MinimalPolynomial.md), [Root](Root.md), [RootApproximant](RootApproximant.md), [RootReduce](RootReduce.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AlgebraicNumberQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/AlgebraicNumberQ.java#L19)

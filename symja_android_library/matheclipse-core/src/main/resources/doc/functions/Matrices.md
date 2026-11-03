## Matrices

```
Matrices({m, n})
```

> represents the domain of the `m` x `n` matrices with complex entries.

```
Matrices({m, n}, dom)
```

> represents the domain of the `m` x `n` matrices with entries in the domain `dom`.

```
Matrices({m, n}, dom, sym)
```

> represents the domain of the `m` x `n` matrices with entries in `dom` and the symmetry `sym`.

`Matrices({m, n})` and `Matrices({m, n}, dom)` evaluate to the three argument form with the empty symmetry `{}`. The dimensions must be a list of length `2`. Use `Element(a, Matrices({m, n}))` to test whether an expression is an `m` x `n` matrix, or as an assumption for `ComponentExpand`, `ArrayExpand`, `ArraySimplify` and functions such as `TensorSymmetry`.

### Examples

```
>> Matrices({2, 3})
Matrices({2,3},Complexes,{})

>> Matrices({n, n}, Reals, Symmetric({1, 2}))
Matrices({n,n},Reals,Symmetric({1,2}))

>> Element(MatrixSymbol(a, {2, 3}), Matrices({2, 3}))
True

>> Element(MatrixSymbol(a, {2, 3}), Matrices({3, 3}))
False
```

If the dimensions agree, a symbolic matrix is in the domain if its entries are known to be in `dom` and it has the same symmetry. Otherwise the test stays unevaluated:

```
>> Element(MatrixSymbol(a, {2, 3}, Reals), Matrices({2, 3}, Reals))
True

>> Element(MatrixSymbol(a, {2, 3}), Matrices({2, 3}, Reals))
MatrixSymbol(a,{2,3})∈Matrices({2,3},Reals,{})

>> Element(MatrixSymbol(q, {3, 3}, Reals, Symmetric({1, 2})), Matrices({3, 3}, Reals))
MatrixSymbol(q,{3,3},Reals,Symmetric({1,2}))∈Matrices({3,3},Reals,{})
```

Explicit and symbolic matrices of integers:

```
>> Element({{1, 2}, {3, 4}}, Matrices({2, 2}, Integers))
True

>> Element(SymbolicIdentityArray({3}), Matrices({3, 3}, Integers))
True
```

`Matrices` as an assumption:

```
>> Assuming(Element(m, Matrices({4, 4}, Reals, Symmetric({1, 2}))), TensorSymmetry(m))
Symmetric({1,2})

>> ComponentExpand(Det(a), Element(a, Matrices({2, 2})))
-Indexed(a,{1,2})*Indexed(a,{2,1})+Indexed(a,{1,1})*Indexed(a,{2,2})

>> ArraySimplify(Det(2 * a) / Det(a), Element(a, Matrices({n, n})))
2^n
```

### Related terms
[Arrays](Arrays.md), [ArraySimplify](ArraySimplify.md), [ComponentExpand](ComponentExpand.md), [Element](Element.md), [MatrixSymbol](MatrixSymbol.md), [Vectors](Vectors.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Matrices](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssumptionFunctions.java#L67)

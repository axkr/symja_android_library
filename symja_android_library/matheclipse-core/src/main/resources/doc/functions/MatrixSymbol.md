## MatrixSymbol

```
MatrixSymbol(a, {m, n})
```

> represents a symbolic `m` x `n` matrix named `a`, whose entries are complex numbers.

```
MatrixSymbol(a, {m, n}, dom)
```

> represents a symbolic `m` x `n` matrix whose entries are in the domain `dom`.

```
MatrixSymbol(a, {m, n}, dom, sym)
```

> represents a symbolic `m` x `n` matrix with entries in `dom` and the symmetry `sym`.

```
MatrixSymbol(a, m)
```

> represents a symbolic square `m` x `m` matrix.

```
MatrixSymbol(a)
```

> represents a symbolic matrix of unknown dimensions.

The dimensions can be positive integers or symbols. The domain `dom` is one of `Complexes`, `Reals`, `Integers`, `NonNegativeReals` or `PositiveReals`. The symmetry `sym` is `None` or one of `Symmetric`, `Antisymmetric`, `Hermitian`, `Antihermitian` or `ZeroSymmetric` applied to `All` or to a list of slots, such as `Symmetric({1, 2})`. The dimensions in the slots of a symmetry must be equal.

With three arguments, the third argument is read as a domain if it is one, and as a symmetry otherwise. Invalid dimensions, domains or symmetries print a message and the expression stays unevaluated.

### Examples

```
>> MatrixSymbol(a, {m, n})
MatrixSymbol(a,{m,n})

>> MatrixSymbol(a, m)
MatrixSymbol(a,{m,m})

>> TensorRank(MatrixSymbol(a))
2
```

A symmetry given without a domain keeps the default domain `Complexes`:

```
>> MatrixSymbol(q, {n, n}, Symmetric({1, 2}))
MatrixSymbol(q,{n,n},Complexes,Symmetric({1,2}))

>> TensorSymmetry(MatrixSymbol(q, {n, n}, Symmetric({1, 2})))
Symmetric({1,2})
```

The symmetry is used by `Transpose`:

```
>> Transpose(MatrixSymbol(q, {n, n}, Reals, Symmetric({1, 2})))
MatrixSymbol(q,{n,n},Reals,Symmetric({1,2}))

>> Transpose(MatrixSymbol(t, {n, n}, Reals, Antisymmetric({1, 2})))
-MatrixSymbol(t,{n,n},Reals,Antisymmetric({1,2}))
```

Arithmetic and matrix products keep track of the dimensions:

```
>> MatrixSymbol(a, {m, n}) - MatrixSymbol(a, {m, n})
SymbolicZerosArray({m,n})

>> TensorDimensions(MatrixSymbol(a, {m, n}) . MatrixSymbol(b, {n, p}))
{m,p}

>> MatrixPower(MatrixSymbol(s, {n, n}), 0)
SymbolicIdentityArray({n})
```

Derivatives with respect to a matrix:

```
>> D(MatrixSymbol(a, {m, n}), MatrixSymbol(a, {m, n}))
SymbolicIdentityArray({m,n})

>> D(Tr(MatrixSymbol(s, {n, n})), MatrixSymbol(s, {n, n}))
SymbolicIdentityArray({n})
```

A matrix of integer dimensions can be written out in its components:

```
>> ComponentExpand(Tr(MatrixSymbol(g, {2, 2})))
Indexed(MatrixSymbol(g,{2,2}),{1,1})+Indexed(MatrixSymbol(g,{2,2}),{2,2})
```

### Related terms
[ArraySymbol](ArraySymbol.md), [ComponentExpand](ComponentExpand.md), [Matrices](Matrices.md), [SymbolicIdentityArray](SymbolicIdentityArray.md), [SymbolicZerosArray](SymbolicZerosArray.md), [TensorDimensions](TensorDimensions.md), [TensorRank](TensorRank.md), [VectorSymbol](VectorSymbol.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of MatrixSymbol](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/MatrixSymbol.java)

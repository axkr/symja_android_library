## ArraySymbol

```
ArraySymbol(a, {n1, n2, ...})
```

> represents a symbolic `n1` x `n2` x ... array named `a`, whose entries are complex numbers.

```
ArraySymbol(a, {n1, n2, ...}, dom)
```

> represents a symbolic array whose entries are in the domain `dom`.

```
ArraySymbol(a, {n1, n2, ...}, dom, sym)
```

> represents a symbolic array with entries in `dom` and the symmetry `sym`.

```
ArraySymbol(a, n)
```

> represents a symbolic vector of length `n`.

```
ArraySymbol(a)
```

> represents a symbolic array of unknown rank and dimensions.

The dimensions can be positive integers or symbols. The domain `dom` is one of `Complexes`, `Reals`, `Integers`, `NonNegativeReals` or `PositiveReals`. The symmetry `sym` is `None` or one of `Symmetric`, `Antisymmetric`, `Hermitian`, `Antihermitian` or `ZeroSymmetric` applied to `All` or to a list of slots, such as `Antisymmetric({1, 2})`. The dimensions in the slots of a symmetry must be equal.

With three arguments, the third argument is read as a domain if it is one, and as a symmetry otherwise. Invalid dimensions, domains or symmetries print a message and the expression stays unevaluated.

### Examples

```
>> ArraySymbol(a, {2, 3, 4})
ArraySymbol(a,{2,3,4})

>> ArraySymbol(a, m)
ArraySymbol(a,{m})

>> TensorDimensions(ArraySymbol(a, {n1, n2, n3}))
{n1,n2,n3}
```

An array with a symmetry in its first two slots:

```
>> ArraySymbol(a, {n, n, p}, Antisymmetric({1, 2}))
ArraySymbol(a,{n,n,p},Complexes,Antisymmetric({1,2}))
```

The dimensions are known to array operations:

```
>> TensorDimensions(TensorProduct(VectorSymbol(v, n), ArraySymbol(a, {p, q})))
{n,p,q}

>> ArrayDot(ArraySymbol(t, {m, p, q, r}), SymbolicIdentityArray({p, q, r}), 3)
ArraySymbol(t,{m,p,q,r})

>> D(Tr(ArraySymbol(a, {m, n, p})), ArraySymbol(a, {m, n, p}))
SymbolicDeltaProductArray({m,n,p},{{1,2,3}})
```

### Related terms
[Arrays](Arrays.md), [ComponentExpand](ComponentExpand.md), [MatrixSymbol](MatrixSymbol.md), [SymbolicDeltaProductArray](SymbolicDeltaProductArray.md), [TensorDimensions](TensorDimensions.md), [TensorRank](TensorRank.md), [VectorSymbol](VectorSymbol.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ArraySymbol](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/ArraySymbol.java)

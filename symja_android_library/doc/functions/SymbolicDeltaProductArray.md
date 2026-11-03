## SymbolicDeltaProductArray

```
SymbolicDeltaProductArray({n1, n2, ...}, {{i1, j1, ...}, {i2, j2, ...}, ...})
```

> represents an `n1` x `n2` x ... array whose entries are the product of one `KroneckerDelta` for every list `{i, j, ...}` of slots. The entry is `1` if the indices in the slots `i, j, ...` of every list are equal and `0` otherwise.

The dimensions can be positive integers or symbols. `Normal` writes the array out if all dimensions are integers. Some derivatives of `Tr`, `Total` and `Mean` with respect to a symbolic array are delta product arrays.

### Examples

With the slots `{1, 2}` paired, a delta product array of rank `2` has ones on its diagonal, also if it is not square:

```
>> Normal(SymbolicDeltaProductArray({2, 2}, {{1, 2}}))
{{1,0},{0,1}}

>> Normal(SymbolicDeltaProductArray({2, 3}, {{1, 2}}))
{{1,0,0},{0,1,0}}
```

The slots `1` and `3` are paired, so slot `2` is free:

```
>> Normal(SymbolicDeltaProductArray({2, 2, 2}, {{1, 3}}))
{{{1,0},{1,0}},{{0,1},{0,1}}}
```

The components as a product of Kronecker deltas:

```
>> Indexed(SymbolicDeltaProductArray({2, 2, 2, 2}, {{1, 4}, {2, 3}}), {i, j, k, l})
KroneckerDelta(i,l)*KroneckerDelta(j,k)

>> Indexed(SymbolicDeltaProductArray({n, n, n}, {{1, 2, 3}}), {i, j, k})
KroneckerDelta(i,j,k)
```

Derivatives which give delta product arrays:

```
>> D(Tr(MatrixSymbol(a, {m, n})), MatrixSymbol(a, {m, n}))
SymbolicDeltaProductArray({m,n},{{1,2}})

>> D(Mean(MatrixSymbol(a, {m, n})), MatrixSymbol(a, {m, n}))
SymbolicDeltaProductArray({n,m,n},{{1,3}})/m
```

### Related terms
[KroneckerDelta](KroneckerDelta.md), [Normal](Normal.md), [SymbolicIdentityArray](SymbolicIdentityArray.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of SymbolicDeltaProductArray](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L2318)

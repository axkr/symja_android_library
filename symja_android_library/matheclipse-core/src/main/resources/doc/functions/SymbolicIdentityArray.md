## SymbolicIdentityArray

```
SymbolicIdentityArray({n})
```

> represents the `n` x `n` identity matrix.

```
SymbolicIdentityArray({n1, n2, ...})
```

> represents the `n1` x `n2` x ... x `n1` x `n2` x ... identity array, whose entry is `1` if the first half of its indices equals the second half and `0` otherwise.

The dimensions can be positive integers or symbols. `Normal` writes the array out if all dimensions are integers. `SymbolicIdentityArray({n1, n2, ...})` is the derivative of an `n1` x `n2` x ... array with respect to itself.

### Examples

```
>> IdentityMatrix(3) == Normal(SymbolicIdentityArray({3}))
True

>> TensorDimensions(SymbolicIdentityArray({n}))
{n,n}

>> TensorDimensions(SymbolicIdentityArray({m, n}))
{m,n,m,n}

>> Normal(SymbolicIdentityArray({2, 2}))
{{{{1,0},{0,0}},{{0,1},{0,0}}},{{{0,0},{1,0}},{{0,0},{0,1}}}}

>> Indexed(SymbolicIdentityArray({n}), {i, j})
KroneckerDelta(i,j)
```

Matrix functions of the identity matrix:

```
>> Det(SymbolicIdentityArray({n}))
1

>> Tr(SymbolicIdentityArray({n}))
n

>> Inverse(SymbolicIdentityArray({n}))
SymbolicIdentityArray({n})

>> MatrixSymbol(a, {m, n}) . SymbolicIdentityArray({n})
MatrixSymbol(a,{m,n})
```

The derivative of an array with respect to itself:

```
>> D(VectorSymbol(v, n), VectorSymbol(v, n))
SymbolicIdentityArray({n})

>> D(MatrixSymbol(a, {m, n}), MatrixSymbol(a, {m, n}))
SymbolicIdentityArray({m,n})
```

### Related terms
[IdentityMatrix](IdentityMatrix.md), [KroneckerDelta](KroneckerDelta.md), [Normal](Normal.md), [SymbolicDeltaProductArray](SymbolicDeltaProductArray.md), [SymbolicOnesArray](SymbolicOnesArray.md), [SymbolicZerosArray](SymbolicZerosArray.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of SymbolicIdentityArray](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L2346)

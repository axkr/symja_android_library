## SymbolicOnesArray

```
SymbolicOnesArray({n1, n2, ...})
```

> represents an `n1` x `n2` x ... array whose entries are all `1`.

The dimensions can be positive integers or symbols. `Normal` writes the array out if all dimensions are integers.

### Examples

```
>> Normal(SymbolicOnesArray({2, 3}))
{{1,1,1},{1,1,1}}

>> Tr(SymbolicOnesArray({n, n}))
n

>> Indexed(SymbolicOnesArray({n, n}), {i, j})
1
```

Elementwise arithmetic with a ones array:

```
>> SymbolicOnesArray({m, n}) * MatrixSymbol(a, {m, n})
MatrixSymbol(a,{m,n})

>> SymbolicOnesArray({m, n})^7
SymbolicOnesArray({m,n})

>> SymbolicOnesArray({m, n}) - 1
SymbolicZerosArray({m,n})
```

`Transpose` and `TensorProduct` of ones arrays:

```
>> Transpose(SymbolicOnesArray({m, n}))
SymbolicOnesArray({n,m})

>> TensorProduct(SymbolicOnesArray({m, n}), SymbolicOnesArray({p}))
SymbolicOnesArray({m,n,p})
```

The gradient of the sum of the components of a vector:

```
>> D(Total(VectorSymbol(v, n)), VectorSymbol(v, n))
SymbolicOnesArray({n})
```

### Related terms
[ConstantArray](ConstantArray.md), [Normal](Normal.md), [SymbolicIdentityArray](SymbolicIdentityArray.md), [SymbolicZerosArray](SymbolicZerosArray.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of SymbolicOnesArray](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L2402)

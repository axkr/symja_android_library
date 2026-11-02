## SymbolicZerosArray

```
SymbolicZerosArray({n1, n2, ...})
```

> represents an `n1` x `n2` x ... array whose entries are all `0`.

The dimensions can be positive integers or symbols. `Normal` writes the array out if all dimensions are integers.

### Examples

```
>> Normal(SymbolicZerosArray({2, 3}))
{{0,0,0},{0,0,0}}

>> Normal(SymbolicZerosArray({m, n}))
SymbolicZerosArray({m,n})

>> TensorDimensions(SymbolicZerosArray({m, n}))
{m,n}
```

Array arithmetic which cancels out gives a zeros array of the same shape and not the scalar `0`:

```
>> MatrixSymbol(a, {m, n}) - MatrixSymbol(a, {m, n})
SymbolicZerosArray({m,n})

>> 0 * MatrixSymbol(a, {m, n})
SymbolicZerosArray({m,n})
```

Arithmetic and matrix functions of a zeros array:

```
>> SymbolicZerosArray({m, n}) + MatrixSymbol(a, {m, n})
MatrixSymbol(a,{m,n})

>> SymbolicZerosArray({m, n}) + 1
SymbolicOnesArray({m,n})

>> SymbolicZerosArray({m, n}) . MatrixSymbol(b, {n, p})
SymbolicZerosArray({m,p})

>> Transpose(SymbolicZerosArray({m, n}))
SymbolicZerosArray({n,m})

>> Tr(SymbolicZerosArray({n, n}))
0

>> Indexed(SymbolicZerosArray({n, n}), {i, j})
0
```

The derivative of a constant with respect to a matrix, or of a matrix with respect to a scalar:

```
>> D(f(x), MatrixSymbol(a, {2, 2}))
SymbolicZerosArray({2,2})

>> D(MatrixSymbol(a, {m, n}), x)
SymbolicZerosArray({m,n})
```

### Related terms
[ConstantArray](ConstantArray.md), [MatrixSymbol](MatrixSymbol.md), [Normal](Normal.md), [SymbolicIdentityArray](SymbolicIdentityArray.md), [SymbolicOnesArray](SymbolicOnesArray.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of SymbolicZerosArray](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L2406)

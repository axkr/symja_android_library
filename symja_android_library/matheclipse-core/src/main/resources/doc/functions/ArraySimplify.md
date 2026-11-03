## ArraySimplify

```
ArraySimplify(expr)
```

> simplifies the symbolic array expression `expr`.

```
ArraySimplify(expr, assum)
```

> simplifies `expr` using the assumptions `assum`.

`ArraySimplify` cancels a double `Transpose` or `Inverse` and a matrix next to its inverse in a `Dot` product, splits the `Det` of a product and the `Tr` of a sum, simplifies the `Det` and `Tr` of transposes and scalar multiples, and factors a common first or last factor out of a sum of `Dot` products. If the result of `ArrayExpand` is shorter, that one is returned. The assumptions can be given as `Element(a, Matrices({m, n}))` or as the option `Assumptions -> ...`.

### Examples

```
>> ArraySimplify(Transpose(Transpose(MatrixSymbol(s, {n, n}))))
MatrixSymbol(s,{n,n})

>> ArraySimplify(Inverse(Inverse(MatrixSymbol(s, {n, n}))))
MatrixSymbol(s,{n,n})

>> ArraySimplify(MatrixSymbol(s, {n, n}) . Inverse(MatrixSymbol(s, {n, n})))
SymbolicIdentityArray({n})
```

Without `ArraySimplify` a double `Inverse` stays as it is:

```
>> Inverse(Inverse(MatrixSymbol(s, {n, n})))
Inverse(Inverse(MatrixSymbol(s,{n,n})))
```

A row vector times a transposed matrix is written as a matrix times a column vector:

```
>> ArraySimplify(VectorSymbol(w, n) . Transpose(MatrixSymbol(s, {n, n})))
MatrixSymbol(s,{n,n}).VectorSymbol(w,n)
```

Determinants and traces:

```
>> ArraySimplify(Det(2 * MatrixSymbol(s, {n, n})) / Det(MatrixSymbol(s, {n, n})))
2^n

>> ArraySimplify(Det(Inverse(MatrixSymbol(s, {n, n}))))
1/Det(MatrixSymbol(s,{n,n}))

>> ArraySimplify(Tr(Transpose(MatrixSymbol(s, {n, n}))))
Tr(MatrixSymbol(s,{n,n}))
```

A common factor of `Dot` products:

```
>> ArraySimplify(a . b + 2 * a . c)
a.(b+2*c)

>> ArraySimplify(b . a + 2 * c . a)
(b+2*c).a
```

With assumptions on plain symbols:

```
>> ArraySimplify(Det(a . b), Element(a | b, Matrices({n, n})))
Det(a)*Det(b)

>> ArraySimplify(a . Inverse(a), Element(a, Matrices({n, n})))
SymbolicIdentityArray({n})
```

### Related terms
[ArrayExpand](ArrayExpand.md), [ComponentExpand](ComponentExpand.md), [Matrices](Matrices.md), [MatrixSymbol](MatrixSymbol.md), [Simplify](Simplify.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ArraySimplify](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SymbolicArrayFunctions.java#L713)

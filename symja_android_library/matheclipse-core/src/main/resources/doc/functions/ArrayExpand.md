## ArrayExpand

```
ArrayExpand(expr)
```

> expands out the symbolic array operations in `expr`.

```
ArrayExpand(expr, assum)
```

> expands out `expr` using the assumptions `assum`.

`ArrayExpand` distributes `Dot` over sums and pulls scalar factors out of it, splits the `Tr` of a sum and the `Det` of a product, and writes the `Inverse` of a product as the product of the inverses in reverse order. The assumptions can be given as `Element(a, Matrices({m, n}))` or as the option `Assumptions -> ...`.

### Examples

```
>> ArrayExpand(MatrixSymbol(s, {n, n}) . (MatrixSymbol(t, {n, n}) + MatrixSymbol(u, {n, n})))
MatrixSymbol(s,{n,n}).MatrixSymbol(t,{n,n})+MatrixSymbol(s,{n,n}).MatrixSymbol(u,{n,n})

>> ArrayExpand(MatrixSymbol(s, {n, n}) . (2 * MatrixSymbol(t, {n, n})))
2*MatrixSymbol(s,{n,n}).MatrixSymbol(t,{n,n})

>> ArrayExpand(Inverse(MatrixSymbol(s, {n, n}) . MatrixSymbol(t, {n, n})))
Inverse(MatrixSymbol(t,{n,n})).Inverse(MatrixSymbol(s,{n,n}))
```

With assumptions on plain symbols:

```
>> ArrayExpand(Det(a . b), Element(a | b, Matrices({n, n})))
Det(a)*Det(b)

>> ArrayExpand(Inverse(a . b), Assumptions -> Element(a | b, Matrices({n, n})))
Inverse(b).Inverse(a)

>> ArrayExpand(Tr(a + b), Element(a | b, Matrices({n, n})))
Tr(a)+Tr(b)
```

### Related terms
[ArraySimplify](ArraySimplify.md), [ComponentExpand](ComponentExpand.md), [Expand](Expand.md), [Matrices](Matrices.md), [MatrixSymbol](MatrixSymbol.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ArrayExpand](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SymbolicArrayFunctions.java#L692)

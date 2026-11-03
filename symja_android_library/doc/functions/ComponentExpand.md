## ComponentExpand

```
ComponentExpand(expr)
```

> writes the symbolic arrays of integer dimensions in `expr` as arrays of their components and evaluates the result.

```
ComponentExpand(expr, assum)
```

> writes out `expr` using the assumptions `assum`.

A component of a symbolic array `a` is written as `Indexed(a, {i, j, ...})`. A declared symmetry is not used to identify components. Arrays with symbolic dimensions stay as they are. The assumptions can be given as `Element(v, Vectors(n))`, `Element(a, Matrices({m, n}))` or as the option `Assumptions -> ...`, so that plain symbols are treated as arrays.

### Examples

```
>> ComponentExpand(VectorSymbol(v, 2))
{Indexed(VectorSymbol(v,2),{1}),Indexed(VectorSymbol(v,2),{2})}

>> ComponentExpand(Tr(MatrixSymbol(g, {2, 2})))
Indexed(MatrixSymbol(g,{2,2}),{1,1})+Indexed(MatrixSymbol(g,{2,2}),{2,2})

>> ComponentExpand(SymbolicOnesArray({2, 3}))
{{1,1,1},{1,1,1}}

>> ComponentExpand(MatrixSymbol(g, {m, n}))
MatrixSymbol(g,{m,n})
```

With assumptions on plain symbols:

```
>> ComponentExpand(v . v, Element(v, Vectors(3)))
Indexed(v,{1})^2+Indexed(v,{2})^2+Indexed(v,{3})^2

>> ComponentExpand(Total(v), Assumptions -> Element(v, Vectors(3)))
Indexed(v,{1})+Indexed(v,{2})+Indexed(v,{3})

>> ComponentExpand(Det(a), Element(a, Matrices({2, 2})))
-Indexed(a,{1,2})*Indexed(a,{2,1})+Indexed(a,{1,1})*Indexed(a,{2,2})

>> ComponentExpand(a . v, Element(a, Matrices({2, 2})) && Element(v, Vectors(2)))
{Indexed(a,{1,1})*Indexed(v,{1})+Indexed(a,{1,2})*Indexed(v,{2}),Indexed(a,{2,1})*Indexed(v,{1})+Indexed(a,{2,2})*Indexed(v,{2})}
```

Without assumptions `v` is not known to be an array:

```
>> ComponentExpand(v . v)
v.v
```

### Related terms
[ArrayExpand](ArrayExpand.md), [ArraySimplify](ArraySimplify.md), [Matrices](Matrices.md), [MatrixSymbol](MatrixSymbol.md), [Normal](Normal.md), [Vectors](Vectors.md), [VectorSymbol](VectorSymbol.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ComponentExpand](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SymbolicArrayFunctions.java#L743)

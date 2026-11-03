## VectorSymbol

```
VectorSymbol(v, n)
```

> represents a symbolic vector of length `n` named `v`, whose components are complex numbers.

```
VectorSymbol(v, n, dom)
```

> represents a symbolic vector of length `n` whose components are in the domain `dom`.

```
VectorSymbol(v)
```

> represents a symbolic vector of unknown length.

The length `n` can be a positive integer or a symbol. `VectorSymbol(v, {n})` is the same as `VectorSymbol(v, n)`. The domain `dom` is one of `Complexes`, `Reals`, `Integers`, `NonNegativeReals` or `PositiveReals`. An invalid length or domain prints a message and the expression stays unevaluated.

Functions such as `Dot`, `D` and `TensorDimensions` work with a symbolic vector without writing it out in components.

### Examples

```
>> VectorSymbol(v, 3)
VectorSymbol(v,3)

>> VectorSymbol(v, {n})
VectorSymbol(v,n)

>> TensorDimensions(VectorSymbol(v, n))
{n}

>> TensorRank(VectorSymbol(v))
1
```

The components of a real vector are known to be real:

```
>> Element(Indexed(VectorSymbol(v, n, Reals), {i}), Reals)
True
```

A symbolic vector is not added to the elements of a list:

```
>> VectorSymbol(v, n) + {1, 2}
{1,2}+VectorSymbol(v,n)
```

Derivatives with respect to a vector:

```
>> D(VectorSymbol(v, n), VectorSymbol(v, n))
SymbolicIdentityArray({n})

>> D(VectorSymbol(v, n) . VectorSymbol(v, n), VectorSymbol(v, n))
2*VectorSymbol(v,n)

>> D(MatrixSymbol(a, {m, n}) . VectorSymbol(v, n), VectorSymbol(v, n))
MatrixSymbol(a,{m,n})
```

A vector of integer length can be written out in its components:

```
>> ComponentExpand(VectorSymbol(v, 2))
{Indexed(VectorSymbol(v,2),{1}),Indexed(VectorSymbol(v,2),{2})}
```

### Related terms
[ArraySymbol](ArraySymbol.md), [ComponentExpand](ComponentExpand.md), [MatrixSymbol](MatrixSymbol.md), [TensorDimensions](TensorDimensions.md), [TensorRank](TensorRank.md), [Vectors](Vectors.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of VectorSymbol](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/VectorSymbol.java)

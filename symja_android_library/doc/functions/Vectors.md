## Vectors

```
Vectors(n)
```

> represents the domain of the vectors of length `n` with complex components.

```
Vectors(n, dom)
```

> represents the domain of the vectors of length `n` with components in the domain `dom`.

`Vectors(n)` evaluates to `Vectors(n, Complexes)`, and `Vectors({n}, dom)` to `Vectors(n, dom)`. Use `Element(v, Vectors(n))` to test whether an expression is a vector of length `n`, or as an assumption for `ComponentExpand`, `ArrayExpand` and `ArraySimplify`.

### Examples

```
>> Vectors(3)
Vectors(3,Complexes)

>> Vectors({n}, Reals)
Vectors(n,Reals)

>> Element(VectorSymbol(v, 3), Vectors(3))
True

>> Element(VectorSymbol(v, 3), Vectors(4))
False
```

An explicit list is in the domain if it has the right length and all its elements are in `dom`:

```
>> Element({1, 2, 3}, Vectors(3, Integers))
True

>> Element({1, Pi, I}, Vectors(3, Reals))
False

>> Element({1, Pi}, Vectors(3))
False
```

A vector with complex components is not known to be real:

```
>> Element(VectorSymbol(v, 3), Vectors(3, Reals))
VectorSymbol(v,3)∈Vectors(3,Reals)
```

`Vectors` as an assumption:

```
>> ComponentExpand(v . v, Element(v, Vectors(3)))
Indexed(v,{1})^2+Indexed(v,{2})^2+Indexed(v,{3})^2
```

### Related terms
[Arrays](Arrays.md), [ComponentExpand](ComponentExpand.md), [Element](Element.md), [Matrices](Matrices.md), [VectorSymbol](VectorSymbol.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Vectors](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssumptionFunctions.java#L104)

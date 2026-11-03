## Arrays

```
Arrays({n1, n2, ...})
```

> represents the domain of the `n1` x `n2` x ... arrays with complex entries.

```
Arrays({n1, n2, ...}, dom)
```

> represents the domain of the `n1` x `n2` x ... arrays with entries in the domain `dom`.

```
Arrays({n1, n2, ...}, dom, sym)
```

> represents the domain of the `n1` x `n2` x ... arrays with entries in `dom` and the symmetry `sym`.

`Arrays(dims)` and `Arrays(dims, dom)` evaluate to the three argument form with the empty symmetry `{}`. Use `Element(a, Arrays(dims))` to test whether an expression is an array of the dimensions `dims`, or as an assumption.

### Examples

```
>> Arrays({2, 3, 4})
Arrays({2,3,4},Complexes,{})

>> Arrays({n, n}, Reals, Symmetric({1, 2}))
Arrays({n,n},Reals,Symmetric({1,2}))

>> Element(ArraySymbol(a, {2, 3, 4}), Arrays({2, 3, 4}))
True

>> Element(ArraySymbol(a, {2, 3, 4}), Arrays({2, 3}))
False
```

Vectors and matrices are arrays as well:

```
>> Element(VectorSymbol(v, 3), Arrays({3}))
True

>> Element(MatrixSymbol(a, {2, 3}), Arrays({2, 3}))
True

>> Element({{1, 2}, {3, 4}}, Arrays({2, 2}, Integers))
True
```

`Arrays` as an assumption:

```
>> Assuming(Element(a, Arrays({2, 3, 4})), TensorDimensions(a))
{2,3,4}
```

### Related terms
[ArraySymbol](ArraySymbol.md), [Element](Element.md), [Matrices](Matrices.md), [TensorDimensions](TensorDimensions.md), [Vectors](Vectors.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Arrays](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssumptionFunctions.java#L43)

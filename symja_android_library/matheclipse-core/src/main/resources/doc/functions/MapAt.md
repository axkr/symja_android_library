## MapAt

```
MapAt(f, expr, n)
```

> applies `f` to the element at position `n` in `expr`. If `n` is negative, the position is counted from the end.
	
```
MapAt(f, expr, {i, j, ...})
```

> applies `f` to the part of `expr` at position `{i, j, ...}`.

```
MapAt(f, pos)
```

> represents an operator form of `MapAt` that can be applied to an expression..

### Examples

Map function `f` to the second element of a simple flat list:

```
>> MapAt(f, {a, b, c}, 2)
{a,f(b),c}
```

Above, we specified a simple integer value `2`. In general, the expression can be an arbitrary vector.

Using `MapAt` with `Function(0)`, we can zero a value or values in a vector:

```
>> MapAt(0&, {{0, 1}, {1, 0}}, {2, 1})
{{0,1},{0,0}}
```

Use the operator form of `MapAt`:

```
>> MapAt(f, -1)[{a, b, c}] 
{a,b,f(c)}
```

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MapAt](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L935) 

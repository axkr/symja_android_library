## Dimensions

```
Dimensions(expr)
```

> returns a list of the dimensions of the expression `expr`.

### Examples

A vector of length 3:

```
>> Dimensions({a, b, c})
{3}
```

A 3x2 matrix:

```
>> Dimensions({{a, b}, {c, d}, {e, f}})
{3, 2}
```

Ragged arrays are not taken into account:

```
>> Dimensions({{a, b}, {b, c}, {c, d, e}})
{3}
```

The expression can have any head:

```
>> Dimensions[f[f[a, b, c]]]
{1, 3}

>> Dimensions({})
{0}

>> Dimensions({{}})
{1, 0}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Dimensions](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L1653) 

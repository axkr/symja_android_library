## Projection

```
Projection(vector1, vector2)
```

> Find the orthogonal projection of `vector1` onto another `vector2`.
 
```
Projection(vector1, vector2, ipf)
```

> Find the orthogonal projection of `vector1` onto another `vector2` using the inner product function `ipf`.

See
* [Wikipedia - Vector projection](https://en.wikipedia.org/wiki/Vector_projection)

### Examples

```
>> Projection({5, I, 7}, {1, 1, 1})
{4+I*1/3,4+I*1/3,4+I*1/3}
```

### Github

* [Implementation of Projection](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L3993) 

## ShearingTransform

```
ShearingTransform(phi, {1, 0}, {0, 1})
```

> gives a horizontal shear by the angle `phi`.

```
ShearingTransform(phi, {0, 1}, {1, 0})
```

> gives a vertical shear by the angle `phi`.

```
ShearingTransform(phi, u, v, p)
```

> gives a shear centered at the point `p`.

See
* [Wikipedia - Transformation matrix](https://en.wikipedia.org/wiki/Transformation_matrix)

### Examples






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ShearingTransform](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L1105) 

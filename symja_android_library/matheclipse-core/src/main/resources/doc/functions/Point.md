## Point

```
Point({point_1, point_2 ...})
```

> represents the point primitive.

```
Point({{p_11, p_12, ...}, {p_21, p_22, ...}, ...})
```

> represents a number of point primitives.
 
### Examples

```
>> Graphics3D({Orange, PointSize(0.05), Point(Table({Sin(t), Cos(t), 0}, {t, 0, 2*Pi, Pi / 15.}))})
 -Graphics3D-
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Point](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphicsFunctions.java#L819) 

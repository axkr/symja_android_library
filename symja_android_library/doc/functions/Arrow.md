## Arrow

```
Arrow({p1, p2})
```

> represents a line from `p1` to `p2` that ends with an arrow at `p2`.

```
Arrow({p1, p2}, s)
```

> represents a line with arrow that keeps a distance of `s` from `p1` and `p2`.

```
Arrow({point_1, point_2}, {s1, s2})
```

> represents a line with arrow that keeps a distance of `s1` from `p1` and a distance of `s2` from `p2`.

```
Arrow({point_1, point_2}, {s1, s2})
```

> represents a line with arrow that keeps a distance of `s1` from `p1` and a distance of `s2` from `p2`.
 
### Examples

Arrows can also be drawn in 3D by giving points in three dimensions:

```
>> Graphics3D(Arrow({{1, 1, -1}, {2, 2, 0}, {3, 3, -1}, {4, 4, 0}}))
 -Graphics3D-
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Arrow](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphicsFunctions.java#L43) 

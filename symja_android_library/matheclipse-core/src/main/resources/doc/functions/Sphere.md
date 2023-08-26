## Sphere

```
Sphere({x, y, z})
```

> is a sphere of radius `1` centered at the point `{x, y, z}`.

```
Sphere({x, y, z}, r)
```

> is a sphere of radius `r` centered at the point `{x, y, z}`.

```
Sphere({{x1, y1, z1}, {x2, y2, z2}, ... }, r)
```

> is a collection of spheres of radius `r` centered at the points `{x1, y2, z2}, {x2, y2, z2}, ... }`
 
### Examples

```
>> Graphics3D(Sphere({0, 0, 0}, 1))
 -Graphics3D-
 
>> Graphics3D({Yellow, Sphere({{-1, 0, 0}, {1, 0, 0}, {0, 0, Sqrt(3.)}}, 1]})
 -Graphics3D-
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Sphere](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphicsFunctions.java#L1120) 

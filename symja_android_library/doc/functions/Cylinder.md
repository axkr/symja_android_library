## Cylinder

```
Cylinder({{x1, y1, z1}, {x2, y2, z2}})
```

> represents a cylinder of radius `1`.

```
Cylinder({{x1, y1, z1}, {x2, y2, z2}}, r)
```

> is a cylinder of radius `r` starting at `{x1, y1, z1}` and ending at `{x2, y2, z2}`.

```
Cylinder({{x1, y1, z1}, {x2, y2, z2}, ... }, r)
```

> is a collection of cylinders of radius `r`
 
### Examples

```
>> Graphics3D(Cylinder({{0, 0, 0}, {1, 1, 1}}, 1))
 -Graphics3D-
 
>> Graphics3D({Yellow, Cylinder({{-1, 0, 0}, {1, 0, 0}, {0, 0, Sqrt(3)}, {1, 1, Sqrt(3}}, 1)})
 -Graphics3D-
```

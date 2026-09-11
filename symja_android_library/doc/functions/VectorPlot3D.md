## VectorPlot3D

```
VectorPlot3D({vx, vy, vz}, {x, xmin, xmax}, {y, ymin, ymax}, {z, zmin, zmax})
```

> draws the vector field `{vx, vy, vz}` as arrows on a regular grid in the given box.

Each arrow is centred on its grid point; the longest spans `VectorScale` (default `0.9`) of the grid spacing, and arrows are coloured by their length unless `VectorColorFunction -> None`. `VectorPoints -> n` (default `7`) or `{nx, ny, nz}` sets the grid. Points where the field is zero or not numeric get no arrow.

### Examples

```
>> VectorPlot3D({x, y, z}, {x, -1, 1}, {y, -1, 1}, {z, -1, 1})
```

### Related terms 
[VectorPlot](VectorPlot.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of VectorPlot3D](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/graphics/VectorPlot.java)

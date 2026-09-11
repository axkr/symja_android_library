## RegionPlot3D

```
RegionPlot3D(pred, {x, xmin, xmax}, {y, ymin, ymax}, {z, zmin, zmax})
```

> draws the solid where the condition `pred` holds, as its surface.

`pred` is made of comparisons like `x*y*z < 1`, `And`, `Or` and `Not`. Where the solid meets the plot box, its surface is closed by a flat cap. `PlotStyle`, `Lighting`, `PlotPoints` and the other `Graphics3D` options are supported.

### Examples

```
>> RegionPlot3D(x^2 + y^2 + z^2 < 1, {x, -1, 1}, {y, -1, 1}, {z, -1, 1})

>> RegionPlot3D(x*y*z < 1, {x, -5, 5}, {y, -5, 5}, {z, -5, 5}, PlotStyle -> Directive(Cyan, "Roughness" -> 0.0), Mesh -> None)
```

### Related terms
[ContourPlot3D](ContourPlot3D.md), [Plot3D](Plot3D.md), [Graphics3D](Graphics3D.md)

### Implementation status

* &#x1F9EA; - experimental

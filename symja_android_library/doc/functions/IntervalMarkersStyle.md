## IntervalMarkersStyle

```
IntervalMarkersStyle -> style
```

> is an option for `Graphics`, `Graphics3D`, `ListPlot`, `ListLinePlot`, `ListPointPlot3D` and `ListPlot3D` that gives the graphics directives the interval markers of uncertain coordinates are drawn with.

With `Automatic` the markers take the style of the points and lines they belong to; a `"Bands"` band is filled translucently in their colour. `ListPlot3D` draws its bars dark, since bars in the colour of the surface would disappear in it.

### Examples

```
>> ListPlot(Table(Around(k, 1), {k, 5}), IntervalMarkers -> "Points", IntervalMarkersStyle -> Red)

>> ListLinePlot(Table(Around(Sin(k/2), 0.2), {k, 0, 12}), IntervalMarkers -> "Bands", IntervalMarkersStyle -> Orange)

>> Graphics3D({Blue, Point({1, 2, Interval({2, 4})})}, IntervalMarkersStyle -> Directive(Red, Thickness(0.01)))
```

### Related terms
[Around](Around.md), [IntervalMarkers](IntervalMarkers.md)

### Implementation status

* &#x2611; - partially implemented

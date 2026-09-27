## PlotMarkers

```
PlotMarkers -> spec
```

> an option for plots, giving the markers drawn at each data point.

The default is `None`, and no markers are drawn. `Automatic` gives a standard sequence of shapes,
one per dataset, so that several datasets in one picture can be told apart in print and by a reader
who cannot rely on the colours.

| setting | meaning |
| --- | --- |
| `None` | no markers |
| `Automatic` | the standard sequence, one shape per dataset, cycling |
| `"OpenMarkers"` | the same sequence unfilled |
| `{Automatic, s}` | the standard sequence at size `s` |
| `g` | a copy of `g` at every point of every dataset |
| `{g, s}` | `g` at size `s` |
| `{g1, g2, ...}` | `gi` for dataset `i`, cycling |
| `{{g1,s1}, {g2,s2}, ...}` | the same, each at its own size |

The standard sequence is a disk, a square, a diamond, and the two triangles. A marker takes the
colour of the curve it belongs to, so the shapes and the colours advance together.

A size is a plain number of printer's points, one of `Tiny`, `Small`, `Medium` and `Large`, an
`Offset(d)` in printer's points, or a `Scaled(s)` fraction of the plot.

`{g, s}` and `{g1, g2}` are written the same way and are told apart by the second element: a size
makes it one marker at that size, anything else makes it two markers.

### Examples

```
>> ListLinePlot({{1,1},{2,4},{3,9}}, PlotMarkers -> Automatic)

>> ListPlot({{{1,1},{2,4}},{{1,2},{2,5}},{{1,3},{2,6}}}, PlotMarkers -> Automatic)

>> ListLinePlot({{1,1},{2,4},{3,9}}, PlotMarkers -> "OpenMarkers")

>> ListLinePlot({{1,1},{2,4},{3,9}}, PlotMarkers -> {"x", 20})

>> ListLinePlot({{1,1},{2,4},{3,9}}, PlotMarkers -> {Automatic, Large})
```

Every point of a dataset carries a marker. A plot that samples a function is another matter - it
can carry a thousand adaptive samples, and a marker on each one is an unreadable smear - so there
the markers are spaced out along the curve, and `Mesh` says how many of them there are:

```
>> Plot(Sin(x), {x, 0, 6}, PlotMarkers -> Automatic)

>> Plot(Sin(x), {x, 0, 6}, PlotMarkers -> Automatic, Mesh -> 30)
```

### Related terms

[Joined](Joined.md), [ListLinePlot](ListLinePlot.md), [ListPlot](ListPlot.md), [Mesh](Mesh.md),
[Plot](Plot.md), [PlotStyle](PlotStyle.md)

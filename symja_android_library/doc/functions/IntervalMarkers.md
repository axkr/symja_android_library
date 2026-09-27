## IntervalMarkers

```
IntervalMarkers -> setting
```

> is an option for `Graphics`, `Graphics3D`, `ListPlot`, `ListLinePlot`, `ListPointPlot3D` and `ListPlot3D` that says how coordinates with an uncertainty are marked.

An uncertain coordinate is drawn at its centre and marked from its lower to its upper limit. It can be given as

* `Around(x, delta)` or `Around(x, {delta-, delta+})` - from `x - delta` to `x + delta`,
* `Interval({a, b})` - from `a` to `b`; the hull of a union of intervals,
* `IntervalData({a, Less | LessEqual, Less | LessEqual, b})` - from `a` to `b`, where an open end (`Less`) gets no fence.

Settings:

| setting | 2D | 3D |
|---|---|---|
| `Automatic` | bars | bars |
| `"Bars"` | a line from the lower to the upper limit on each uncertain axis | the same |
| `"Fences"` | bars with a cap at each closed limit | bars |
| `"Points"` | a point at each limit | bars |
| `"Ellipses"` | an ellipse inside the limits of both coordinates | bars |
| `"Bands"` | a translucent band through the limits of a line or of a set of points | a translucent strip through the `z` limits of a line |
| `"Tubes"` | bars | a tube around each bar |
| `None` | only the centre | only the centre |

See
* [Wikipedia - Error bar](https://en.wikipedia.org/wiki/Error_bar)

### Examples

```
>> Graphics(Point({{1, Around(2, 0.5)}, {2, Interval({2, 4})}, {3, IntervalData({1, Less, LessEqual, 2})}}), IntervalMarkers -> "Fences", Axes -> True)

>> ListPlot(Table(Around(Sin(k/2), 0.1 + 0.05*Abs(Cos(k))), {k, 0, 12}))

>> ListLinePlot(Table(Around(Sin(k/2), 0.2), {k, 0, 12}), IntervalMarkers -> "Bands")

>> ListPlot(Table({k, Around(Sqrt(k), 0.2)}, {k, 1, 8}), IntervalMarkers -> "Ellipses")

>> Graphics3D(Line(Table({Cos(t), Sin(t), Around(t/3, 0.3)}, {t, 0, 6, 0.25})), IntervalMarkers -> "Bands")

>> ListPointPlot3D(Table({Cos(t), Sin(t), Around(t/3, 0.3)}, {t, 0, 6, 0.5}), IntervalMarkers -> "Tubes")
```

### Related terms
[Around](Around.md), [Interval](Interval.md), [IntervalData](IntervalData.md), [IntervalMarkersStyle](IntervalMarkersStyle.md), [ListPlot](ListPlot.md)

### Implementation status

* &#x2611; - partially implemented

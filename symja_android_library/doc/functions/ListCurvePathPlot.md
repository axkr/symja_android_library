## ListCurvePathPlot

```
ListCurvePathPlot({{x1, y1}, {x2, y2}, ...})
```

> plots the curves the points `{xi, yi}` lie on, whatever order they are given in.

The curves are rebuilt from the points. Each point is joined to its nearest neighbours, shortest joins first, as long as no point gets more than two joins, no loop closes, a join is at most about two and a half times the typical distance between neighbouring points, and a curve turns by less than 60 degrees at a point. Points sampled from a curve come back as that curve; scattered points come back as many short curves, and a point that joins nothing is left out. All the curves are drawn in one style; the options are those of `ListLinePlot`.

### Examples

A circle, from its points given in scrambled order:

```
>> pts = Table({Cos(2*Pi*k/40), Sin(2*Pi*k/40)}, {k, 0, 39})[[Mod(7*Range(40), 40) + 1]];

>> ListCurvePathPlot(pts)
```

### Related terms
[ListLinePlot](ListLinePlot.md), [ListPlot](ListPlot.md)

### Implementation status

* &#x1F9EA; - experimental

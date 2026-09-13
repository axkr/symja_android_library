## BubbleChart

```
BubbleChart({{x1, y1, z1}, {x2, y2, z2}, ...})
```

> draws the points `{x, y}` as bubbles whose area stands for `z`.

```
BubbleChart({{{x, y, z}, ...}, {{x, y, z}, ...}})
```

> draws several datasets, each in a colour of its own.

The bubbles run from a hundredth to a tenth of the width of the data. `ChartStyle`, `ChartLegends`, `ChartBaseStyle` and `ChartElementFunction` are supported; of the named element functions `"NoiseBubble"` draws a wobbly rim.

### Examples

```
>> BubbleChart({{1, 1, 1}, {2, 2, 4}, {3, 1, 9}})

>> BubbleChart(RandomReal(1, {10, 3}), ChartElementFunction -> "NoiseBubble")
```

### Related terms
[BarChart](BarChart.md), [PieChart](PieChart.md), [SectorChart](SectorChart.md), [ListPlot](ListPlot.md)

### Implementation status

* &#x1F9EA; - experimental

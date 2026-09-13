## SectorChart

```
SectorChart({{angle1, radius1}, {angle2, radius2}, ...})
```

> draws a pie whose sectors differ in radius too: the first number of a pair is what the angle of the sector is proportional to, the second is how far it reaches.

The sectors start at the left and follow one another clockwise, as in Mathematica. `SectorOrigin -> {angle, r}` starts them at another angle and leaves a hole of radius `r`, which turns the sectors into rings. `PolarAxes` and `PolarGridLines` draw the polar scale behind them, and `ChartStyle`, `ChartLabels`, `ChartLegends` and `ChartBaseStyle` work as in the other charts.

### Examples

```
>> SectorChart({{1, 1}, {1, 2}, {1, 3}})

>> SectorChart({{1, 1}, {1, 2}, {1, 3}}, SectorOrigin -> {Automatic, 1})

>> SectorChart({{1, 1}, {2, 2}}, SectorOrigin -> {Pi/2, "Clockwise"}, PolarAxes -> True, PolarGridLines -> Automatic)
```

### Related terms
[PieChart](PieChart.md), [BarChart](BarChart.md), [BubbleChart](BubbleChart.md)

### Implementation status

* &#x1F9EA; - experimental

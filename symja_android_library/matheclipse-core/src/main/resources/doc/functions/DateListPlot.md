## DateListPlot

```
DateListPlot({{date1, v1}, {date2, v2}, ...})
```

> plots the values `vi` against the dates `datei`, joined by a line.

```
DateListPlot({series1, series2, ...})
```

> plots several series of dated values.

A date can be a `DateObject`, a date list `{y, m, d, h, min, s}`, a date string or an absolute time (seconds since the start of 1900, as `AbsoluteTime` counts them). As in the Wolfram Language, the plot is framed rather than drawn with axes, and the dates are written under the bottom of the frame, at whole years, months, days or hours depending on how long the data runs. The options are those of `ListLinePlot`; an option written in the call replaces these defaults.

### Examples

```
>> DateListPlot({{DateObject({2022, 12}), 1}, {DateObject({2023, 12}), 2}, {DateObject({2026, 9, 11}), 3}})

>> DateListPlot({{{2020, 1, 1}, 1}, {{2020, 7, 1}, 4}, {{2021, 1, 1}, 2}})
```

### Related terms
[AbsoluteTime](AbsoluteTime.md), [DateObject](DateObject.md), [ListLinePlot](ListLinePlot.md)

### Implementation status

* &#x1F9EA; - experimental

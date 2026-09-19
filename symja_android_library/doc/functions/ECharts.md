## ECharts
 
```
ECharts(graphics)
```

> draws the 2D plot `graphics` as an [Apache ECharts](https://echarts.apache.org) chart instead of as SVG.

The result is a `JSFormData(javascript, "echarts")` expression. The web front ends show it in a sandboxed frame that loads ECharts from its CDN, with a button that opens the chart in [JSFiddle](https://jsfiddle.net).

ECharts draws series of data, so it takes what plots are made of: lines, points and labels. Each curve becomes a line series and each point set a scatter series, on value or logarithmic axes spanning the plot range, named after the `PlotLegends` of the plot. A graphic holding other shapes - the bars of a `BarChart`, a filled region - is left unevaluated with a message.

`ECharts` is part of the optional `matheclipse-jsgraphics` module; without it the expression stays unevaluated.

### Examples

```
>> ECharts(ListLinePlot({1, 4, 9, 16}))

>> ECharts(Plot({Sin(x), Cos(x)}, {x, 0, 2*Pi}, PlotLegends -> {"sin", "cos"}))

>> ECharts(LogPlot(Exp(x), {x, 0, 5}))
```

### Related terms 
[Graphics](Graphics.md), [JSXGraph](JSXGraph.md), [ListPlot](ListPlot.md), [MathCell](MathCell.md), [Plot](Plot.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of ECharts](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-jsgraphics/src/main/java/org/matheclipse/jsgraphics/echarts/EChartsRenderer.java) 

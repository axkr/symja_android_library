## JSXGraph
 
```
JSXGraph(graphics)
```

> draws the 2D `graphics` with the [JSXGraph](https://jsxgraph.org) JavaScript library instead of as SVG.

The result is a `JSFormData(javascript, "jsxgraph")` expression. The web front ends show it in a sandboxed frame that loads JSXGraph from its CDN, with a button that opens the picture in [JSFiddle](https://jsfiddle.net). The board can be panned and zoomed.

The graphic is laid out exactly as for the SVG picture, so plot range, colours, line styles, points, text, arrows and axes agree with it. Not drawn: `Raster`, `Inset`, logarithmic axes, and the holes of a `Polygon`.

`JSXGraph` is part of the optional `matheclipse-jsgraphics` module; without it the expression stays unevaluated.

### Examples

```
>> JSXGraph(Plot(Sin(x), {x, 0, 2*Pi}))

>> JSXGraph(Graphics({Red, Disk({0, 0}, 1), Blue, Arrow({{0, 0}, {2, 2}})}))
```

### Related terms 
[ECharts](ECharts.md), [Graphics](Graphics.md), [JSForm](JSForm.md), [MathCell](MathCell.md), [Plot](Plot.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of JSXGraph](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-jsgraphics/src/main/java/org/matheclipse/jsgraphics/jsxgraph/JSXGraphRenderer.java) 

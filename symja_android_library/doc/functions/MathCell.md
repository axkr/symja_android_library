## MathCell
 
```
MathCell(plot)
```

> draws `plot` with the [MathCell](https://github.com/paulmasson/mathcell) JavaScript library, which evaluates functions in the browser with the [math](https://github.com/paulmasson/math) library.

`MathCell` holds its argument. When it is a `Plot`, `ParametricPlot` or `Plot3D` whose function translates to JavaScript (see `JSForm(expr, "Mathcell")`), the function itself is handed to MathCell, which samples it in the browser; a `Plot3D` becomes a surface that can be turned with the mouse. Anything else is evaluated first, and the resulting 2D `Graphics` is drawn from its lines, points, polygons and text.

The result is a `JSFormData(javascript, "mathcell")` expression. The web front ends show it in a sandboxed frame that loads the libraries from their CDN, with a button that opens the cell in [JSFiddle](https://jsfiddle.net).

`MathCell` is part of the optional `matheclipse-jsgraphics` module; without it the expression stays unevaluated.

### Examples

```
>> MathCell(Plot(Sin(x), {x, 0, 2*Pi}))

>> MathCell(Plot3D(Sin(x*y), {x, -2, 2}, {y, -2, 2}))

>> MathCell(Graphics({Red, Line({{0, 0}, {1, 1}}), Point({1, 0})}))
```

### Related terms 
[ECharts](ECharts.md), [Graphics](Graphics.md), [JSForm](JSForm.md), [JSXGraph](JSXGraph.md), [Plot](Plot.md), [Plot3D](Plot3D.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of MathCell](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-jsgraphics/src/main/java/org/matheclipse/jsgraphics/mathcell/MathCellRenderer.java) 

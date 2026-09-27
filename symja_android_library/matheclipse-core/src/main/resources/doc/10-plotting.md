## Plotting graphs and functions

 
- [Examples](#examples) 
- [Used JavaScript libraries](#used-javascript-libraries) 
- [Generating JavaScript output](#generating-javascript-output) 

## Examples

These are some functions integrated in Symja, which allow the output of a graphical "JavaScript control".  

* [BarChart](functions/BarChart.md)			
* [BoxWhiskerChart](functions/BoxWhiskerChart.md)	
* [DensityHistogram](functions/DensityHistogram.md)	
* [Histogram](functions/Histogram.md)	
* [PieChart](functions/PieChart.md)	
* [ListLinePlot](functions/ListLinePlot.md)
* [ListPlot](functions/ListPlot.md)
* [ListPointPlot3D](functions/ListPointPlot3D.md)
* [Manipulate](functions/Manipulate.md)
* [ParametricPlot](functions/ParametricPlot.md) 
* [Plot](functions/Plot.md) 
* [Plot3D](functions/Plot3D.md)
 
Here are some corresponding examples:

```			
>> ListLinePlot(Table({n, n ^ 0.5}, {n, 10})) 
		
>> Manipulate(ListPlot(Table({Sin(t), Cos(t*a)}, {t, 100})), {a,1,4,1})
		
>> Manipulate(ListPointPlot3D(Table({Sin(t), Cos(t*a), Cos(t^2) }, {t, 500})), {a,1,4,1})
		
>> Manipulate(Plot3D(Sin(a*x*y), {x, -1.5, 1.5}, {y, -1.5, 1.5}), {a,1,5})
		
>> ParametricPlot({Sin(t), Cos(t^2)}, {t, 0, 2*Pi}) 
		
>> Plot(Sin(x)*Cos(1 + x), {x, 0, 2*Pi})

>> Graphics3D({Darker(Yellow), Sphere({{-1, 0, 0}, {1, 0, 0}, {0, 0, Sqrt(3.0)}}, 1)})
```
 
The following example displays an undirected weighted [Graph](functions/Graph.md) from graph theory functions:

```			
>> Graph({1 <-> 2, 2 <-> 3, 3 <-> 4, 4 <-> 1},{EdgeWeight->{2.0,3.0,4.0, 5.0}})   
```

[TreeForm](functions/TreeForm.md) visualizes the structure of an expression:

```
>> TreeForm(a+(b*q*s)^(2*y)+Sin(c)^(3-z)) 
```

## JavaScript libraries

Graphics are drawn as SVG, and 3D graphics with WebGL. With the optional `matheclipse-jsgraphics` module a 2D graphic can be drawn by a JavaScript library instead, which runs in a sandboxed frame that loads the library from its CDN and has a button that opens the example in [JSFiddle](https://jsfiddle.net):

- [JSXGraph](functions/JSXGraph.md) draws it with [JSXGraph](https://github.com/jsxgraph/jsxgraph), as a board that can be panned and zoomed
- [ECharts](functions/ECharts.md) draws a plot of lines and points as an [Apache ECharts](https://echarts.apache.org) chart
- [MathCell](functions/MathCell.md) draws it with [MathCell](https://github.com/paulmasson/mathcell), and hands the function of a `Plot` or `Plot3D` to the browser, where the [Math](https://github.com/paulmasson/math) library evaluates it

```
>> JSXGraph(Plot(Sin(x)*Cos(1 + x), {x, 0, 2*Pi}))

>> MathCell(Plot3D(Sin(x*y), {x, -1.5, 1.5}, {y, -1.5, 1.5}))
```

## Generating JavaScript output

To use a formula in your own web pages, [JSForm](functions/JSForm.md) translates it to JavaScript:

```
>> JSForm(Sin(x)*Cos(1 + x))

>> JSForm(Sin(x)*Cos(1 + x), "Mathcell")
```

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

## Used JavaScript libraries

For the generation of these controls, Symja uses the following JavaScript libraries

- [Math](https://github.com/paulmasson/math) for evaluating common mathematical operations in JavaScript 
- [JSXGraph](https://github.com/jsxgraph/jsxgraph) for  interactive function plotting and charting 
- [MathCell](https://github.com/paulmasson/mathcell) for displaying 3D function plots
- [vis-network](https://github.com/visjs/vis-network) for (graph) network views

You can get the HTML source code of the generated controls in most browser by doing a right-mouse-click on the control and selecting menu: `This Frame -> View Frame Source` and save the HTML source code as standalone example on your file system.

## Generating JavaScript output

If you would like to use the output from the plot or graph functions in your own web pages, you can generate the JavaScript source code with the [JSForm](functions/JSForm.md) function.

You can for example display the generated JavaScript form of the [Manipulate](functions/Manipulate.md) function:

```
>> Manipulate(Plot(Sin(x)*Cos(1 + a*x), {x, 0, 2*Pi}), {a,0,10}) // JSForm
```

and insert it in a HTML template from the [JSXGraph.org](https://jsxgraph.org/wp/index.html) project.

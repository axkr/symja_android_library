## Plotting graphs and functions

These are some functions integrated in Symja, which allow the output of a graphical "JavaScript control".  

* [ListLinePlot](functions/ListLinePlot.md)
* [ListPlot](functions/ListPlot.md)
* [ListPlot3D](functions/ListPlot3D.md)
* [Manipulate](functions/Manipulate.md)
* [ParametricPlot](functions/ParametricPlot.md) 
* [Plot](functions/Plot.md) 
* [Plot3D](functions/Plot3D.md)
 
Here are some corresponding examples:

```			
>> ListLinePlot(Table({n, n ^ 0.5}, {n, 10})) 
```


```			
>> Manipulate(ListPlot(Table({Sin(t), Cos(t*a)}, {t, 100})), {a,1,4,1})
```


```			
>> Manipulate(ListPlot3D(Table({Sin(t), Cos(t*a), Cos(t^2) }, {t, 500})), {a,1,4,1})
```

```			
>> Manipulate(Plot3D(Sin(a*x*y), {x, -1.5, 1.5}, {y, -1.5, 1.5}), {a,1,5})
```

```			
>> ParametricPlot({Sin(t), Cos(t^2)}, {t, 0, 2*Pi}) 
```

```			
>> Plot(Sin(x)*Cos(1 + x), {x, 0, 2*Pi})
```
 
The following example displays an undirected weighted [Graph](functions/Graph.md) from graph theory functions:

```			
>> Graph({1 <-> 2, 2 <-> 3, 3 <-> 4, 4 <-> 1},{EdgeWeight->{2.0,3.0,4.0, 5.0}})   
```

[TreeForm](functions/TreeForm.md) visualizes the structure of an expression:

```
>> TreeForm(a+(b*q*s)^(2*y)+Sin(c)^(3-z)) 
```
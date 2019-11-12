## Plotting graphs and functions

There are some functions integrated in Symja, which allows the output of some "JavaScript control". Here are some examples:

With the [Manipulate](functions/Manipulate.md) function

```
>> Manipulate(plot, {x, min, max})
```

you can generate a JavaScript control for the expression `plot` which can be manipulated by a visual range slider `{x, min, max}`.



```
>> Plot(function, {x, xMin, xMax}, PlotRange->{yMin,yMax})  
```

The [Plot](functions/Plot.md) function plots the expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.


```
>> ParametricPlot({function1, function2}, {t, tMin, tMax})
```

The [ParametricPlot](functions/ParametricPlot.md) function plots the parametric expressions `function1`, `function2` in the t range `{t, tMin, tMax}`.

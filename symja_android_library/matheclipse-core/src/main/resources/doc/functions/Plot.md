## Plot

```
Plot(function, {x, xMin, xMax}, PlotRange->{yMin,yMax})  
```

> generate a JavaScript control for the expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
	 
**Note**: This feature is available in the console app and in the web interface.

### Examples

In the console apps, this command shows an HTML page with a JavaScript plot control.
 
```
>> Plot(Sin(x)*Cos(1 + x), {x, 0, 2*Pi}) 
```

With `JSForm` you can display the generated JavaScript form of the `Manipulate` function

```
>> Plot(Sin(x)*Cos(1 + x), {x, 0, 2*Pi}) // JSForm
```

### Related terms 
[JSForm](JSForm.md) [Manipulate](Manipulate.md) [ParametricPlot](ParametricPlot.md) [Plot3D](Plot3D.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Plot](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Plot.java#L26) 

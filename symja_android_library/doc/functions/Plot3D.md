## Plot3D

```
Plot3D(function, {x, xMin, xMax}, {y,yMin,yMax})  
```

> generate a JavaScript control for the expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.

```
Plot3D(function, {x, xMin, xMax}, {y,yMin,yMax}, ColorFunction->"color-map")  
```

> set the `color map` as: `CherryTones, Rainbow, RustTones, SunsetColors, TemperatureMap, ThermometerColors, WatermelonColors`

					
**Note**: This function is available in the console app and in the web interface.

### Examples

In the console apps, this command shows an HTML page with a JavaScript of a 3D surface plot control:

```
>> Plot3D(Sin(x*y), {x, -1.5, 1.5}, {y, -1.5, 1.5}) 
```

With `JSForm` you can display the generated JavaScript form of the `Manipulate` function

```
>> Plot3D(Sin(x*y), {x, -1.5, 1.5}, {y, -1.5, 1.5}) // JSForm
```

### Related terms 
[JSForm](JSForm.md) [Manipulate](Manipulate.md) [ParametricPlot](ParametricPlot.md) [Plot](Plot.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Plot3D](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Plot3D.java#L28) 

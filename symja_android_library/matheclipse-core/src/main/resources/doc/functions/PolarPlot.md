## PolarPlot

```
PolarPlot(function, {t, tMin, tMax})  
```

> generate a JavaScript control for the polar plot  expressions `function` in the `t` range `{t, tMin, tMax}`.

### Examples

This command shows an HTML page with a JavaScript parametric plot control.
 
```
>> PolarPlot(1-Cos(t), {t, 0, 2*Pi})
```

With `JSForm` you can display the generated JavaScript form of the `PolarPlot` function

```
>> PolarPlot(1-Cos(t), {t, 0, 2*Pi}) // JSForm
```

### Related terms 
[JSForm](JSForm.md), [Manipulate](Manipulate.md), [ParametricPlot](ParametricPlot.md) [Plot](Plot.md), [Plot3D](Plot3D.md)
## ListLinePlot

```
ListLinePlot( { list-of-points } )  
```

> generate a JavaScript list line plot control for the `list-of-points`.
	 
**Note**: This feature is available in the console app and in the web interface.

### Examples

In the console apps, this command shows an HTML page with a JavaScript list line plot control.

```
>> Manipulate(ListLinePlot(Table({Sin(t), Cos(t*a)}, {t, 50})), {a,1,4,1})
```

With `JSForm` you can display the generated JavaScript form of the `Manipulate` function

```
>> Manipulate(ListLinePlot(Table({Sin(t), Cos(t*a)}, {t, 50})), {a,1,4,1}) // JSForm
```

### Related terms 
[JSForm](JSForm.md) [Manipulate](Manipulate.md) [ParametricPlot](ParametricPlot.md) [Plot](Plot.md)  [Plot3D](Plot3D.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ListLinePlot](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/ListLinePlot.java#L13) 

## DiscretePlot
 
```
DiscretePlot( expr, {x, nmax} )  
```

> plots `expr` with `x` ranging from `1` to `nmax`.

```
DiscretePlot( expr, {x, nmin, nmax} )  
```

> plots `expr` with `x` ranging from `nmin` to `nmax`.

```
DiscretePlot( expr, {x, nmin, nmax, delta} )  
```

> plots `expr` with `x` ranging from `nmin` to `nmax` usings steps `delta`.
	 
**Note**: This feature is available in the console app and in the web interface.

### Examples

In the console apps, this command shows an HTML page with a JavaScript list plot control.
 
```
>> DiscretePlot(Sin(x), {x, 0, 4 Pi}, PlotRange->{{0, 4 Pi}, {0, 1.5}})
```


```
>> DiscretePlot(Tan(x), {x, -6, 6})
```

### Related terms 
[ListPlot](ListPlot.md), [ListLogPlot](ListLogPlot.md)
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DiscretePlot](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/DiscretePlot.java#L12) 

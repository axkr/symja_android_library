## JSForm
 
```
JSForm(expr)
```

> returns the JavaScript form of the `expr`.  

```
JSForm(expr, "Mathcell")
```

> returns the JavaScript form of the `expr` with 'Mathcell' flavor output.  

JSForm generates JavaScript output for the following JavaScript libraries:
* [github.com/jsxgraph/jsxgraph](https://github.com/jsxgraph/jsxgraph)
* [github.com/paulmasson/math](https://github.com/paulmasson/math) 
* [github.com/paulmasson/mathcell](https://github.com/paulmasson/mathcell) 
	  
The "Mathcell" flavour is what [MathCell](MathCell.md) hands to the browser when it plots a function. To draw a graphic with a JavaScript library use [JSXGraph](JSXGraph.md), [ECharts](ECharts.md) or [MathCell](MathCell.md).
	 
See:  
* [developer.mozilla.org - Global Objects Math](https://developer.mozilla.org/de/docs/Web/JavaScript/Reference/Global_Objects/Math) 

### Examples 

Generate output for JavaScript floating-point arithmetic expressions:

```
>> JSForm(E^3-Cos(Pi^2/x)) 
(20.085536923187664)-Math.cos((9.869604401089358)/x)
```

Generate output for MathCell and Math JavaScript libraries:

```
>> JSForm(4*EllipticE(x)+KleinInvariantJ(t)^3, "Mathcell")
add(mul(4,ellipticE(x)),pow(kleinJ(t),3))
```

### Related terms 
[ECharts](ECharts.md), [JSXGraph](JSXGraph.md), [MathCell](MathCell.md) 






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of JSForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/OutputFunctions.java#L643) 

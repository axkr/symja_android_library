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
	  
This JavaScript flavour is also used in the `[Manipulate](Manipulate.md)` function.
	 
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

With `JSForm` you can display the generated JavaScript form of the `Manipulate` function

```
>> Manipulate(Plot(Sin(x)*Cos(1 + a*x), {x, 0, 2*Pi}), {a,0,10}) // JSForm
```

### Related terms 
[Manipulate](Manipulate.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of JSForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/OutputFunctions.java#L550) 

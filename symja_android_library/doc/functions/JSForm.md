## JSForm
 
```
JSForm(expr)
```

> returns the JavaScript form of the `expr`.  

```
JSForm(expr, "Mathcell")
```

> generate JavaScript output for the [github.com/paulmasson/math](https://github.com/paulmasson/math) and [github.com/paulmasson/mathcell](https://github.com/paulmasson/mathcell) JavaScript libraries. This JavaScript flavour is also used in the `Manipulate` function.
	 
	 
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
## JSForm
 
```
JSForm(expre)
```

> returns the JavaScript form of the `expr`.  

See:  
* [developer.mozilla.org - Global Objects Math](https://developer.mozilla.org/de/docs/Web/JavaScript/Reference/Global_Objects/Math) 

### Examples 

Generate output for JavaScript floating-point arithmetic expressions:

```
>> JSForm(E^3-Cos(Pi^2/x), Float) 
Math.pow(Math.E,3)-Math.cos(Math.pow(Math.PI,2)/x)
```

With `JSForm` you can display the generated JavaScript form of the `Manipulate` function

```
>> Manipulate(Plot(Sin(x)*Cos(1 + a*x), {x, 0, 2*Pi}), {a,0,10}) // JSForm
```

### Related terms 
[Manipulate](Manipulate.md) 
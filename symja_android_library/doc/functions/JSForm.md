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


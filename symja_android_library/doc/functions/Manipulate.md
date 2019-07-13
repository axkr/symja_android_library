## Manipulate

```
Manipulate(plot, {x, min, max})  
```

> generate a JavaScript control for the expression `plot` which can be manipulated by a range slider `{x, min, max}`.
	 
**Note**: This feature is not available on all supported platforms.

### Examples

In the console apps, this command shows an HTML page with a JavaScript plot control, 
where the value of the variable `a` can be manipulated by a slider in the range `[0..10]`.
 
```
>> Manipulate(Plot(Sin(x)*Cos(1 + a*x), {x, 0, 2*Pi}), {a,0,10}) 

```
  
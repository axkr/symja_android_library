## Manipulate

```
Manipulate(plot, {x, min, max})  
```

> generate a JavaScript control for the expression `plot` which can be manipulated by a range slider `{x, min, max}`.


```
Manipulate(formula, {x, min, max, step})  
```

> display generated `formula`s and define the `step`s in which the values for `x` should change.
	 
**Note**: This feature is not available on all supported platforms.

Manipulate generates JavaScript output for the following JavaScript libraries:

* [github.com/jsxgraph/jsxgraph](https://github.com/jsxgraph/jsxgraph)
* [github.com/paulmasson/math](https://github.com/paulmasson/math) 
* [github.com/paulmasson/mathcell](https://github.com/paulmasson/mathcell) 

### Examples

In the console apps, this command shows an HTML page with a JavaScript plot control, 
where the value of the variable `a` can be manipulated by a slider in the range `[0..10]`.
 
```
>> Manipulate(Plot(Sin(x)*Cos(1 + a*x), {x, 0, 2*Pi}), {a,0,10}) 
```

A 3D surface plot control:

```
>> Manipulate(Plot3D(Sin(a*x*y), {x, -1.5, 1.5}, {y, -1.5, 1.5}), {a,1,5})
```

Display a slider for the generated formulas:

```
>> Manipulate(Factor(x^n + 1), {n, 1, 5, 1})
```

Display buttons to change the plotted function:

```
>> Manipulate(Plot(f(x), {x, 0, 2*Pi}), {f, {Sin, Cos, Tan, Cot}})
```

With `JSForm` you can display the generated JavaScript form of the `Manipulate` function

```
>> Manipulate(Plot(Sin(x)*Cos(1 + a*x), {x, 0, 2*Pi}), {a,0,10}) // JSForm
```

### Related terms 
[JSForm](JSForm.md), [ParametricPlot](ParametricPlot.md), [Plot](Plot.md), [Plot3D](Plot3D.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Manipulate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ManipulateFunction.java#L1944) 

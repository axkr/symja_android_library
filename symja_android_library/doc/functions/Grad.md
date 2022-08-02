## Grad

```
Grad(function, list-of-variables)
```

> gives the gradient of the function.

```
Grad({f1, f2,...},  {v1, v2,...)
```

> returns the Jacobian matrix for the vector of functions.

See:  
* [Wikipedia - Gradient](https://en.wikipedia.org/wiki/Gradient)

### Examples

```
>> Grad(2*x+3*y^2-Sin(z), {x, y, z})
{2,6*y,-Cos(z)}
```

Create a Jacobian matrix:

```
>> Grad({f(x, y),g(x,y)}, {x, y})
{{Derivative(1,0)[f][x,y],Derivative(0,1)[f][x,y]},{Derivative(1,0)[g][x,y],Derivative(0,1)[g][x,y]}}
```

### Github

* [Implementation of Grad](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/VectorAnalysisFunctions.java#L137) 

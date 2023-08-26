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
* [Wikipedia - Jacobian matrix](https://en.wikipedia.org/wiki/Jacobian_matrix_and_determinant)

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

Example from Wikipedia where the Jacobian matrix doesn't need to be squared:

```
>> Grad({x1,5*x3,4*x2^2-2*x3,x3*Sin[x1]},{x1,x2,x3}) 
{{1,0,0},{0,0,5},{0,8*x2,-2},{x3*Cos(x1),0,Sin(x1)}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Grad](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/VectorAnalysisFunctions.java#L185) 
